package com.ablil.springstarter.authentication.services

import com.ablil.springstarter.InvalidCredentials
import com.ablil.springstarter.authentication.controllers.Credentials
import com.ablil.springstarter.common.JwtUtil
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.repositories.UserRepository
import jakarta.validation.Valid
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
) {
    fun validateCredentialsAndGenerateToken(
        @Valid credentials: Credentials,
    ): String {
        validateCredentials(credentials)
        return JwtUtil.generate(credentials.username)
    }

    private fun validateCredentials(credentials: Credentials) {
        val user = userRepository.findByUsernameOrEmail(credentials.username)
            ?: throw InvalidCredentials("account not found")

        if (user.status != AccountStatus.ACTIVE) {
            throw InvalidCredentials("account disabled")
        }
        if (!passwordEncoder.matches(credentials.password, user.password)) {
            throw InvalidCredentials("invalid credentials")
        }
    }
}
