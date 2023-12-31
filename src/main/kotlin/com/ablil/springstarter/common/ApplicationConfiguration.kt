package com.ablil.springstarter.common

import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.entities.UserEntity
import com.ablil.springstarter.persistence.entities.UserRole
import com.ablil.springstarter.persistence.repositories.UserRepository
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ApplicationProperties::class)
class ApplicationConfiguration(
    val userRepository: UserRepository,
    val applicationProperties: ApplicationProperties
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    @PostConstruct
    fun createAdminUser() {
        userRepository.findByUsername("admin") ?: userRepository.save(
            UserEntity(
                id = null,
                username = "admin",
                password = applicationProperties.adminPassword,
                email = "admin@app.com",
                role = UserRole.ADMIN,
                status = AccountStatus.ACTIVE
            )
        ).also {
            logger.info("Admin user created")
        }
    }
}
