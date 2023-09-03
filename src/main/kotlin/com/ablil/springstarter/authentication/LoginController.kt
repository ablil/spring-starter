package com.ablil.springstarter.authentication

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class LoginController(
    private val loginService: LoginService
) {
    @PostMapping("login")
    fun login(@RequestBody @Valid credentials: LoginCredentials): Token {
        val token = loginService.login(credentials)
        return Token(token)
    }

    @PostMapping("forget_password")
    fun forgetPassword(@RequestBody @Valid body: EmailDto) {
        loginService.forgetPassword(body.email)
    }

    @PostMapping("reset_password")
    fun resetPassword(@RequestBody @Valid body: ResetPassword) {
        loginService.resetPassword(body)
    }
}

data class Token(val token: String)

data class EmailDto(@Email val email: String)

data class ResetPassword(@NotBlank val token: String, @NotBlank @Size(min = 10) val password: String)

data class LoginCredentials(@NotBlank val usernameOrEmail: String, @NotBlank val password: String)