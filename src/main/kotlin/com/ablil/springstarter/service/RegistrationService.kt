package com.ablil.springstarter.service

import com.ablil.springstarter.common.TokenNotFound
import com.ablil.springstarter.common.UserAlreadyExists
import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.entities.UserEntity
import com.ablil.springstarter.persistence.repositories.UserRepository
import com.ablil.springstarter.webapi.RegistrationRequest
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegistrationService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val mailService: MailService?
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun register(request: RegistrationRequest): UserEntity {
        if (userRepository.findByUsernameOrEmail(request.username, request.email) != null) {
            throw UserAlreadyExists(request.username)
        }
        val userEntity = UserEntity(
            id = null,
            username = request.username,
            password = passwordEncoder.encode(request.password),
            token = RandomStringUtils.randomAlphanumeric(10),
            email = request.email,
        )

        return userRepository.save(userEntity).also {
            logger.info("created new account for ${request.username}")
            mailService?.confirmRegistration(userEntity.email, requireNotNull(userEntity.token))
        }
    }

    fun confirmRegistration(token: String) {
        userRepository.findByToken(token)?.also {
            userRepository.save(it.copy(token = null, status = AccountStatus.ACTIVE))
        } ?: throw TokenNotFound("token $token not found")
    }
}
