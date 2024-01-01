package com.ablil.springstarter.common

import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.entities.UserEntity
import com.ablil.springstarter.persistence.entities.UserRole
import com.ablil.springstarter.persistence.repositories.UserRepository
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration(val userRepository: UserRepository) {
    @Value("\${app.audit.password}")
    private lateinit var password: String

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    @PostConstruct
    fun createAdminUser() {
        userRepository.findByUsername("admin") ?: userRepository.save(
            UserEntity(
                id = null,
                username = "admin",
                password = password,
                email = "admin@app.com",
                role = UserRole.ADMIN,
                status = AccountStatus.ACTIVE,
            ),
        ).also {
            logger.info("Admin user created")
        }
    }
}
