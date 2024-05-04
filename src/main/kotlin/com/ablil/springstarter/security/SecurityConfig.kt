package com.ablil.springstarter.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.context.annotation.RequestScope

@Configuration
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            authorizeRequests {
                authorize("/error", permitAll)

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
            oauth2ResourceServer { jwt { } }
        }
        http.formLogin { it.disable() }
        return http.build()
    }

    @Bean
    fun passwordEncoder() = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    @RequestScope
    fun authenticatedUser(): UserDetails {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication is JwtAuthenticationToken) {
            val jwt = authentication.credentials as org.springframework.security.oauth2.jwt.Jwt
            return User(
                authentication.name,
                jwt.tokenValue,
                AuthorityUtils.createAuthorityList(
                    *jwt.getClaim<String>("scope").split(" ").toTypedArray(),
                ),
            )
        }
        error("Unknown type of authentication $authentication")
    }
}
