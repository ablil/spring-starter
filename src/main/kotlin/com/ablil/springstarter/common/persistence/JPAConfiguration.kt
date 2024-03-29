package com.ablil.springstarter.common.persistence

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import java.util.Optional

@Configuration
@EnableJpaAuditing
class JPAConfiguration() {
    @Bean
    fun auditorProvider(): AuditorAware<String> = AuditorAware<String> {
        when (val principal = SecurityContextHolder.getContext().authentication?.principal) {
            is UserDetails -> Optional.of(principal.username.toString())
            is String -> Optional.ofNullable(principal)
            else -> Optional.ofNullable("SYSTEM")
        }
    }
}
