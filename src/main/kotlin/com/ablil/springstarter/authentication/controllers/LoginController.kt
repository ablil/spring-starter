package com.ablil.springstarter.authentication.controllers

import com.ablil.springstarter.authentication.services.LoginService
import com.ablil.springstarter.webapi.api.LoginApi
import com.ablil.springstarter.webapi.model.LoginRequest
import com.ablil.springstarter.webapi.model.Token
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController(val loginService: LoginService) : LoginApi {
    override fun login(loginRequest: LoginRequest): ResponseEntity<Token> {
        val credentials = LoginCredentials(loginRequest.username, loginRequest.password)
        val token = loginService.login(credentials).let(::Token)
        return ResponseEntity.ok(token)
    }
}

@Schema(name = "login credentials")
data class LoginCredentials(
    @NotBlank val usernameOrEmail: String,
    @NotBlank val password: String,
)
