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

    fun register(username: String, plainPassword: String) {
        val account = Account(id = null, username = username, password = passwordEncoder.encode(plainPassword))
        accountRepository.save(account).also { logger.info("registered user $username") }
    }
}