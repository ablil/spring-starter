package com.ablil.springstarter.config

import com.ablil.springstarter.filters.BearerTokenFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    @Autowired val bearerTokenFilter: BearerTokenFilter
) {

    @Bean
    fun getSecurityConfig(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it.requestMatchers("/health", "/error").permitAll()
                .anyRequest().authenticated()
        }
            .addFilterBefore(bearerTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}