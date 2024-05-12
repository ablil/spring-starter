package com.ablil.springstarter.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.context.annotation.RequestScope

@Configuration
class SecurityConfig {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun apiSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            securityMatcher("/api/**")
            authorizeRequests { authorize(anyRequest, authenticated) }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            oauth2ResourceServer { jwt { } }
            csrf { disable() }
        }
        return http.formLogin { it.disable() }.build()
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    fun fallbackSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            authorizeRequests {
                authorize("/error", permitAll)

                authorize("/swagger-ui/**", permitAll)
                authorize("/todos.yml", permitAll)
                authorize("/v3/api-docs/**", permitAll)

                authorize("/actuator/health/**", permitAll)
                authorize("/actuator/**", hasAnyAuthority("ADMIN", "TECHNICAL"))

                authorize(anyRequest, authenticated)
            }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            csrf { disable() }
            oauth2ResourceServer { jwt { } }
        }
        http.formLogin { it.disable() }
        return http.build()
    }

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
        if (authentication is UsernamePasswordAuthenticationToken) {
            return authentication.principal as UserDetails
        }
        error("Unknown type of authentication $authentication")
    }
}
