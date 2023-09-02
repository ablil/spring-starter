package com.ablil.springstarter.authentication

import com.ablil.springstarter.domain.users.UserRepository
import com.ablil.springstarter.miscllaneous.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class LoginService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder
) {

    fun login(credentials: LoginCredentials): String {
        val (identifier, password) = credentials
        val user = userRepository.findByUsernameOrEmail(identifier, identifier)?.takeIf {
            passwordEncoder.matches(password, it.password)
        } ?: throw InvalidCredentials()

        return JwtUtil.generate(user.username)
    }
}