package com.ablil.springstarter.webapi

import com.ablil.springstarter.authentication.LoginService
import com.ablil.springstarter.miscllaneous.ConfigParams
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Authentication")
@RestController
@RequestMapping("/auth")
class LoginController(
        private val loginService: LoginService,
) {
    @PostMapping("login")
    fun login(
            @RequestBody @Valid
        credentials: LoginCredentials,
            response: HttpServletResponse
    ): ResponseEntity<Void> {
        val token = loginService.login(credentials)
        response.addCookie(
            Cookie("jwt", token).apply {
                path = "/"
                maxAge = ConfigParams.MAX_TOKEN_AGE
                secure = true
            }
        )
        return ResponseEntity.noContent().build()
    }

    @PostMapping("forget_password")
    @Operation(summary = "Trigger forget password process", description = "Send email with reset link")
    fun forgetPassword(
            @RequestBody @Valid
        body: EmailDto,
    ) {
        loginService.forgetPassword(body.email)
    }

    @PostMapping("reset_password")
    @Operation(summary = "reset password", description = "Given reset token, reset password")
    fun resetPassword(
            @RequestBody @Valid
        body: ResetPassword,
    ) {
        loginService.resetPassword(body)
    }
}

@Schema(name = "json web token")
data class Token(val token: String)

@Schema(name = "email")
data class EmailDto(@Email val email: String)

@Schema(name = "reset password request")
data class ResetPassword(@NotBlank val token: String, @NotBlank @Size(min = 10) val password: String)

@Schema(name = "login credentials")
data class LoginCredentials(@NotBlank val usernameOrEmail: String, @NotBlank val password: String)
