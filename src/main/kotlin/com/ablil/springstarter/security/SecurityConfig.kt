package com.ablil.springstarter.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter
import org.springframework.security.web.header.HeaderWriterFilter

@Configuration
class SecurityConfig {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager,
    ): SecurityFilterChain {
        http.invoke {
            authorizeRequests {
                PUBLIC_ENDPOINTS.forEach { authorize(it, permitAll) }
                TECHNICAL_ENDPOINTS.forEach { authorize(it, hasAnyAuthority("ADMIN", "TECHNICAL")) }
                authorize(anyRequest, authenticated)
            }
            csrf { disable() }
            httpBasic { }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            addFilterBefore<HeaderWriterFilter>(
                requestHeaderAuthenticationFilter(
                    authenticationManager,
                ),
            )
        }
        http.formLogin { it.disable() }
        return http.build()
    }

    @Bean
    fun authenticationManager(
        authenticationTokenProvider: AuthenticationTokenProvider,
        daoAuthenticationProvider: DaoAuthenticationProvider,
    ) = ProviderManager(listOf(authenticationTokenProvider, daoAuthenticationProvider))

    @Bean
    fun daoAuthenticationProvider(userDetailsService: DefaultUserDetailsService) =
        DaoAuthenticationProvider().apply {
            setPasswordEncoder(passwordEncoder())
            setUserDetailsService(userDetailsService)
        }

    @Bean
    fun requestHeaderAuthenticationFilter(authenticationManager: AuthenticationManager) =
        RequestHeaderAuthenticationFilter().apply {
            setPrincipalRequestHeader("Authorization")
            setExceptionIfHeaderMissing(false)
            setAuthenticationManager(authenticationManager)
        }

    @Bean
    fun passwordEncoder() = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    companion object {
        val PUBLIC_ENDPOINTS = arrayOf(
            "/health",
            "/error",
            "/actuator/health",
            "/api/auth/register/confirm",
            "/api/auth/register/**",
            "/api/auth/login",
            "/api/auth/forget_password",
            "/api/auth/reset_password",
        )
        val TECHNICAL_ENDPOINTS = arrayOf(
            "/admin",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/users/**",
        )
    }
}
