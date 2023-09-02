package com.ablil.springstarter.authentication

import com.ablil.springstarter.domain.users.User
import com.ablil.springstarter.domain.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegistrationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun register(request: RegistrationRequest): User {
        val user = User(
            id = null,
            username = request.username,
            password = passwordEncoder.encode(request.password),
        )
        return userRepository.save(user).also { logger.info("created new account for ${request.username}") }
    }
}