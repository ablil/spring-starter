package com.ablil.springstarter.service

import com.ablil.springstarter.common.InvalidCredentials
import com.ablil.springstarter.common.ResetPasswordError
import com.ablil.springstarter.miscllaneous.JwtUtil
import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.repositories.UserRepository
import com.ablil.springstarter.webapi.LoginCredentials
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LoginService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val mailService: MailService?
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
        }?.also { mailService?.resetPassword(email, resetToken) }
    }

    @Transactional
    fun resetPassword(token: String, password: String) {
        val user = userRepository.findByToken(token) ?: throw ResetPasswordError("token $token not found")
        userRepository.updateTokenAndStatus(null, AccountStatus.ACTIVE, user.email)
        userRepository.resetPassword(passwordEncoder.encode(password), user.email)
        mailService?.passwordHasBeenReset(user.email)
    }
}
