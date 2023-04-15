package com.ablil.springstarter.domain.accounts

import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun register(username: String, plainPassword: String): Account {
        val account = Account(
            id = null,
            username = username,
            password = passwordEncoder.encode(plainPassword),
            status = AccountStatus.INACTIVE,
            firstName = null,
            lastName = null,
        )
        return accountRepository.save(account).also { logger.info("registered user $username") }
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