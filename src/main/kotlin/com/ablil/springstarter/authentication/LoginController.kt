package com.ablil.springstarter.authentication

import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class LoginController(
    private val loginService: LoginService
) {
    // TODO: configure slf4j or logging

    @PostMapping("login")
    @Operation(summary = "authenticate user")
    fun login(@RequestBody @Valid credentials: LoginCredentials): Token {
        val token = loginService.login(credentials)
        return Token(token)
    }

}

data class Token(val token: String)

data class LoginCredentials(@NotBlank val username: String, @NotBlank val password: String)