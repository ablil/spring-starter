package com.ablil.springstarter.authentication.services

import com.ablil.springstarter.ResetPasswordError
import com.ablil.springstarter.TokenNotFound
import com.ablil.springstarter.UserAlreadyExists
import com.ablil.springstarter.authentication.controllers.RegistrationDto
import com.ablil.springstarter.mail.MailService
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.repositories.UserRepository
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegistrationService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val mailService: MailService,
) {
    fun registerNewUser(
        @Valid request: RegistrationDto,
    ) {
        checkIfUserExists(request)
        val registrationToken = RandomStringUtils.randomAlphanumeric(10)
        userRepository.save(
            UserEntity(
                id = null,
                username = request.username,
                password = passwordEncoder.encode(request.password),
                token = registrationToken,
                email = request.email,
                status = AccountStatus.INACTIVE,
            ),
        )
        mailService.confirmRegistration(request.email, registrationToken)
    }

    private fun checkIfUserExists(request: RegistrationDto) {
        if (userRepository.findByUsername(request.username) != null) {
            throw UserAlreadyExists(request.username)
        }
        if (userRepository.findByEmail(request.email) != null) {
            throw UserAlreadyExists(request.username)
        }
    }

    fun confirmUserRegistration(token: String) {
        val user =
            userRepository.findByToken(token) ?: throw TokenNotFound("token $token not found")
        userRepository.save(user.copy(token = null, status = AccountStatus.ACTIVE))
    }

    @Transactional
    fun sendPasswordResetLink(email: String) {
        val user = userRepository.findByEmail(email)
        if (user == null) {
            logger.warn("user with email $email not found, ignore forget password request")
            return
        }

        val resetToken = RandomStringUtils.randomAlphanumeric(10)
        userRepository.save(
            user.copy(token = resetToken, status = AccountStatus.PASSWORD_RESET_IN_PROGRESS),
        )
        mailService.sendResetPasswordLink(email, resetToken)
    }

    @Transactional
    fun resetUserPassword(token: String, password: String) {
        val user = userRepository.findByToken(token) ?: throw ResetPasswordError("Invalid token")
        userRepository.save(
            user.copy(
                token = null,
                status = AccountStatus.ACTIVE,
                password = passwordEncoder.encode(password),
            ),
        )
        mailService.notifyUserWithPasswordChange(user.email)
    }

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)
    }
}
