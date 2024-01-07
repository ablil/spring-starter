package com.ablil.springstarter.common

import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.entities.UserEntity
import com.ablil.springstarter.persistence.entities.UserRole
import com.ablil.springstarter.persistence.repositories.UserRepository
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class ApplicationConfiguration(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
) {
    @Value("\${app.audit.password}")
    private lateinit var password: String

    @PostConstruct
    fun createAdminUser() {
        with(userRepository) {
            findByUsername("admin") ?: save(
                UserEntity(
                    id = null,
                    username = "admin",
                    password = passwordEncoder.encode(password),
                    email = "admin@app.com",
                    role = UserRole.ADMIN,
                    status = AccountStatus.ACTIVE,
                ),
            ).also { logger.info("Admin user created") }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}
