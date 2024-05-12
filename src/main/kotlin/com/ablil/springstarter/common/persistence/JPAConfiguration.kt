package com.ablil.springstarter.common.persistence

import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.repositories.TodoRepository
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.util.*

@Configuration
@EnableJpaRepositories(basePackageClasses = [TodoRepository::class])
@EntityScan(basePackageClasses = [TodoEntity::class])
@EnableJpaAuditing
class JPAConfiguration {
    @Bean
    fun auditorProvider(): AuditorAware<String> = AuditorAware<String> {
        val jwt = SecurityContextHolder.getContext().authentication
            .takeIf { it is JwtAuthenticationToken }
            ?.let { it.principal as Jwt }
        Optional.ofNullable(jwt?.subject)
    }
}
