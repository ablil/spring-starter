package com.ablil.springstarter.authentication.controllers

import com.ablil.springstarter.authentication.services.AuthenticationService
import com.ablil.springstarter.webapi.api.LoginApi
import com.ablil.springstarter.webapi.model.LoginRequest
import com.ablil.springstarter.webapi.model.Token
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Authentication")
class AuthenticationController(val authenticationService: AuthenticationService) : LoginApi {
    override fun login(loginRequest: LoginRequest): ResponseEntity<Token> {
        val credentials = Credentials(loginRequest.username, loginRequest.password)
        val token = authenticationService.validateCredentialsAndGenerateToken(credentials)
        return ResponseEntity.ok(Token(token))
    }
}

data class Credentials(
    @NotBlank val username: String,
    @NotBlank val password: String,
)
