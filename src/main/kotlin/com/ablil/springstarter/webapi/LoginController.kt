package com.ablil.springstarter.webapi

import com.ablil.springstarter.service.LoginService
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
        val credentials = LoginCredentials(loginRequest.usernameOrEmail, loginRequest.password)
        val token = loginService.login(credentials).let(::Token)
        return ResponseEntity.ok(token)
    }
}

@Schema(name = "login credentials")
data class LoginCredentials(
    @NotBlank val usernameOrEmail: String,
    @NotBlank val password: String,
)
