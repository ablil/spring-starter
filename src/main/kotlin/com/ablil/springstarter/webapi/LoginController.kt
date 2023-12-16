package com.ablil.springstarter.webapi

import com.ablil.springstarter.miscllaneous.ConfigParams
import com.ablil.springstarter.service.LoginService
import com.ablil.springstarter.webapi.api.LoginApi
import com.ablil.springstarter.webapi.model.LoginRequest
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController(
    private val loginService: LoginService,
    private val response: HttpServletResponse
) : LoginApi {
    override fun authLoginPost(loginRequest: LoginRequest): ResponseEntity<Unit> {
        val token = loginService.login(
            LoginCredentials(
                usernameOrEmail = loginRequest.usernameOrEmail,
                password = loginRequest.password
            )
        )
        response.addCookie(
            Cookie("jwt", token).apply {
                path = "/"
                maxAge = ConfigParams.MAX_TOKEN_AGE
                secure = true
            }
        )
        return ResponseEntity.noContent().build()
    }
}

@Schema(name = "login credentials")
data class LoginCredentials(@NotBlank val usernameOrEmail: String, @NotBlank val password: String)