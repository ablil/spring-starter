package com.ablil.springstarter.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter
import org.springframework.security.web.header.HeaderWriterFilter
import org.springframework.web.context.annotation.RequestScope

@Configuration
class SecurityConfig {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager,
    ): SecurityFilterChain {
        http.invoke {
            authorizeRequests {
                authorize("/error", permitAll)

                authorize("/api/auth/**", permitAll)
                authorize("/api/todos/**", hasAuthority("DEFAULT"))
                authorize("/api/users/**", hasAnyAuthority("ADMIN", "TECHNICAL"))

                authorize("/swagger-ui/**", hasAnyAuthority("ADMIN", "TECHNICAL"))
                authorize("/v3/api-docs/**", hasAnyAuthority("ADMIN", "TECHNICAL"))

                authorize("/actuator/health/**", permitAll)
                authorize("/actuator/**", hasAnyAuthority("ADMIN", "TECHNICAL"))

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

    @Bean
    @RequestScope
    fun authenticatedUser(): UserDetails {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication.principal is UserDetails) return authentication.principal as UserDetails
        error("User not authenticated or unknown principal type")
    }
}
