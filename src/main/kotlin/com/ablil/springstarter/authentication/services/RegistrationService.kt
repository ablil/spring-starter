package com.ablil.springstarter.authentication.services

import com.ablil.springstarter.TokenNotFound
import com.ablil.springstarter.UserAlreadyExists
import com.ablil.springstarter.mail.MailService
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.repositories.UserRepository
import com.ablil.springstarter.webapi.model.RegistrationRequest
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegistrationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val mailService: MailService,
) {
    fun register(request: RegistrationRequest): UserEntity {
        val existingUser = userRepository.findByUsernameOrEmail(request.username)
            ?: userRepository.findByUsernameOrEmail(request.email)
        if (existingUser != null) throw UserAlreadyExists(request.username)

        val savedUser = userRepository.save(
            UserEntity(
                id = null,
                username = request.username,
                password = passwordEncoder.encode(request.password),
                token = RandomStringUtils.randomAlphanumeric(10),
                email = request.email,
            ),
        )
        mailService.confirmRegistration(savedUser.email, requireNotNull(savedUser.token))
        return savedUser
    }

    fun confirmRegistration(token: String) {
        with(userRepository) {
            val user = findByToken(token) ?: throw TokenNotFound("token $token not found")
            save(
                user.copy(
                    token = null,
                    status = com.ablil.springstarter.users.entities.AccountStatus.ACTIVE,
                ),
            )
        }
    }
}
