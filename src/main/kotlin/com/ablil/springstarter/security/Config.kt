package com.ablil.springstarter.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class Config(
    private val bearerTokenFilter: BearerTokenFilter
) {

    private val publicEndpoints = arrayOf(
        "/health",
        "/error",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/auth/register/**",
        "/auth/login",
        "/auth/forget_password",
        "/auth/reset_password",
    )

    @Bean
    fun getSecurityConfig(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            authorizeRequests {
                publicEndpoints.forEach { path -> authorize(path, permitAll) }
                authorize(anyRequest, authenticated)
            }
            csrf { disable() }
            httpBasic { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(bearerTokenFilter)
        }
        http.formLogin { it.disable() }
        return http.build()
    }

    @Bean
    fun getPasswordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
}
