package com.ablil.springstarter.persistence

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.Optional

@Configuration
@EnableJpaAuditing
class JPAConfiguration {

    @Bean
    fun auditorProvider(): AuditorAware<String> = AuditorAware<String> {
        // TODO: get principal from security context
        Optional.of("SYSTEM")
    }
}
