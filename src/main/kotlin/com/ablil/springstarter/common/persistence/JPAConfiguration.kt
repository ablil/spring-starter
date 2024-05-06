package com.ablil.springstarter.common.persistence

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.util.Optional

@Configuration
@EnableJpaAuditing
class JPAConfiguration() {
    @Bean
    @Profile("!test")
    fun auditorProvider(): AuditorAware<String> = AuditorAware<String> {
        val jwt = SecurityContextHolder.getContext().authentication
            .takeIf { it is JwtAuthenticationToken }
            ?.let { it.principal as Jwt }
        Optional.ofNullable(jwt?.subject)
    }

    @Bean
    @Profile("test")
    fun integrationAuditorProvider() = AuditorAware { Optional.of("johndoe") }
}
