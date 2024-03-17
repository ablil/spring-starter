package com.ablil.springstarter.authentication.services

import com.ablil.springstarter.InvalidCredentials
import com.ablil.springstarter.ResetPasswordError
import com.ablil.springstarter.authentication.controllers.LoginCredentials
import com.ablil.springstarter.common.JwtUtil
import com.ablil.springstarter.mail.MailService
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.repositories.UserRepository
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LoginService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val mailService: MailService,
) {
    fun login(credentials: LoginCredentials): String {
        val (identifier, password) = credentials
        val user = userRepository.findByUsernameOrEmail(identifier)
            ?: throw InvalidCredentials()

        when {
            !passwordEncoder.matches(password, user.password) -> throw InvalidCredentials()
            user.status != AccountStatus.ACTIVE -> throw InvalidCredentials()
        }

        return JwtUtil.generate(user.username)
    }

    @Transactional
    fun forgetPassword(email: String) {
        val resetToken = RandomStringUtils.randomAlphanumeric(10)
        with(userRepository) {
            findByEmail(email)?.let {
                updateTokenAndStatus(
                    resetToken,
                    com.ablil.springstarter.users.entities.AccountStatus.PASSWORD_RESET_IN_PROGRESS,
                    email,
                )
            }?.also { mailService.resetPassword(email, resetToken) }
        }
    }

    @Transactional
    fun resetPassword(token: String, password: String) {
        val user = userRepository.findByToken(token)
            ?: throw ResetPasswordError("token $token not found")

        with(userRepository) {
            updateTokenAndStatus(
                null,
                com.ablil.springstarter.users.entities.AccountStatus.ACTIVE,
                user.email,
            )
            resetPassword(passwordEncoder.encode(password), user.email)
        }.also { mailService.passwordHasBeenReset(user.email) }
    }
}
