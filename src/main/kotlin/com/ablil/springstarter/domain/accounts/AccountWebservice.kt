package com.ablil.springstarter.domain.accounts

import com.ablil.springstarter.domain.accounts.dtos.RegistrationRequest
import com.ablil.springstarter.domain.accounts.dtos.Token
import com.ablil.springstarter.domain.accounts.dtos.UsernameAndPassword
import com.ablil.springstarter.domain.accounts.exceptions.InvalidCredentials
import com.ablil.springstarter.utils.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountWebservice(
    private val accountService: AccountService
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody body: RegistrationRequest) {
        accountService.register(body)
    }

    @PostMapping("/login")
    fun login(@RequestBody body: UsernameAndPassword): Token {
        if (accountService.authenticate(body.username, body.password)) {
            val token = JwtUtil.generate(body.username)
            return Token(token)
        } else {
            throw InvalidCredentials()
        }
    }
}