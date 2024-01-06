package com.ablil.springstarter.service

import com.ablil.springstarter.common.TokenNotFound
import com.ablil.springstarter.common.UserAlreadyExists
import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.entities.UserEntity
import com.ablil.springstarter.persistence.repositories.UserRepository
import com.ablil.springstarter.webapi.model.RegistrationRequest
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegistrationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val mailService: MailService?,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun register(request: RegistrationRequest): UserEntity {
        if (userRepository.findByUsernameOrEmail(request.username, request.email) != null) {
            throw UserAlreadyExists(request.username)
        }
        val savedUser = userRepository.save(
            UserEntity(
                id = null,
                username = request.username,
                password = passwordEncoder.encode(request.password),
                token = RandomStringUtils.randomAlphanumeric(10),
                email = request.email,
            ),
        )
        mailService?.confirmRegistration(savedUser.email, requireNotNull(savedUser.token))
        return savedUser
    }

    fun confirmRegistration(token: String) {
        with(userRepository) {
            val user = findByToken(token) ?: throw TokenNotFound("token $token not found")
            save(user.copy(token = null, status = AccountStatus.ACTIVE))
        }
    }
}
