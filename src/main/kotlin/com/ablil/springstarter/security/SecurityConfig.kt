package com.ablil.springstarter.security

import com.ablil.springstarter.security.filters.JsonWebTokenFilter
import com.ablil.springstarter.security.filters.LogRequestsFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val jsonWebTokenFilter: JsonWebTokenFilter,
    private val logRequestsFilter: LogRequestsFilter,
) {
    companion object {
        val publicEndpoints = arrayOf(
            "/health",
            "/error",
            "/actuator/health",
            "/api/auth/register/**",
            "/api/auth/login",
            "/api/auth/forget_password",
            "/api/auth/reset_password",
        )
        val privateEndpoints = arrayOf(
            "/admin",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/users/**",
        )
    }

    @Bean
    @Order(2)
    fun createAPISecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            securityMatcher("/api/**")
            authorizeRequests {
                publicEndpoints
                    .filter { it.startsWith("/api") }
                    .forEach { authorize(it, permitAll) }
                authorize(anyRequest, authenticated)
            }
            csrf { disable() }
            httpBasic { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(logRequestsFilter)
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jsonWebTokenFilter)
        }
        http.formLogin { it.disable() }
        return http.build()
    }

    @Bean
    @Order(1)
    fun createAdminSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            securityMatcher(*privateEndpoints)
            authorizeRequests {
                authorize("/actuator/health", permitAll)
                authorize(anyRequest, hasAuthority("ADMIN"))
            }
            httpBasic {}
        }
        http.formLogin { it.disable() }
        return http.build()
    }

    @Bean
    @Order(3)
    fun createFallbackSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            authorizeRequests {
                publicEndpoints.forEach { authorize(it, permitAll) }
                authorize(anyRequest, denyAll)
            }
            csrf { disable() }
            httpBasic { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(logRequestsFilter)
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jsonWebTokenFilter)
        }
        http.formLogin { it.disable() }
        return http.build()
    }

    @Bean
    fun getPasswordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()
}
