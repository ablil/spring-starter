package com.ablil.springstarter.miscllaneous

import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.entities.User
import com.ablil.springstarter.persistence.repositories.UserRepository
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@Profile("dev")
class TestUser(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun createTestUser() {
        userRepository.save(
            User(
                id = null,
                username = "joedoe",
                email = "joedoe@example.com",
                password = passwordEncoder.encode("supersecurepassword"),
                status = AccountStatus.ACTIVE,
                token = null,
            ),
        )
            .also { logger.info("Created test user username=joedoe, email=joedoe@example.com, password=supersecurepassword") }
    }
}
