package com.ablil.springstarter.domain.accounts

import com.ablil.springstarter.domain.accounts.dtos.RegistrationRequest
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun register(req: RegistrationRequest): Account {
        val account = Account(
            id = null,
            username = req.username,
            password = passwordEncoder.encode(req.password),
            status = AccountStatus.INACTIVE,
            firstName = req.firstName?.trim(),
            lastName = req.lastName?.trim(),
        )
        return accountRepository.save(account).also { logger.info("registered user ${req.username}") }
    }

    fun register(username: String, password: String): Account {
        return register(RegistrationRequest.withUsernameAndPassword(username, password))
    }

    fun authenticate(username: String, plainPassword: String): Boolean {
        return accountRepository.findByUsername(username)
            ?.let { passwordEncoder.matches(plainPassword, it.password) && it.status != AccountStatus.INACTIVE }
            ?: false;
    }

    fun activate(username: String) {
        accountRepository.findByUsername(username)
            ?.copy(status = AccountStatus.ACTIVE)
            ?.also { accountRepository.save(it) }
    }
}