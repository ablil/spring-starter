package com.ablil.springstarter.authentication

import com.ablil.springstarter.domain.users.AccountStatus
import com.ablil.springstarter.domain.users.UserRepository
import com.ablil.springstarter.miscllaneous.EmailClient
import com.ablil.springstarter.miscllaneous.JwtUtil
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LoginService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val emailClient: EmailClient,
) {

    fun login(credentials: LoginCredentials): String {
        val (identifier, password) = credentials
        val user = userRepository.findByUsernameOrEmail(identifier, identifier)?.takeIf {
            passwordEncoder.matches(password, it.password)
        } ?: throw InvalidCredentials()

        return JwtUtil.generate(user.username)
    }

    @Transactional
    fun forgetPassword(email: String) {
        val resetToken = RandomStringUtils.randomAlphanumeric(10)
        userRepository.findByEmail(email)?.let {
            userRepository.updateTokenAndStatus(resetToken, AccountStatus.PASSWORD_RESET_IN_PROGRESS, email)
        }?.also {
            emailClient.sendEmail(email, "Reset password", resetToken)
        }
    }

    @Transactional
    fun resetPassword(body: ResetPassword) {
        val user = userRepository.findByToken(body.token) ?: throw ResetPasswordError("token ${body.token} not found")
        userRepository.updateTokenAndStatus(null, AccountStatus.ACTIVE, user.email)
        userRepository.resetPassword(passwordEncoder.encode(body.password), user.email)
    }
}
