package com.ablil.springstarter.authentication.controllers

import com.ablil.springstarter.authentication.services.RegistrationService
import com.ablil.springstarter.webapi.api.RegistrationApi
import com.ablil.springstarter.webapi.model.ForgetPasswordRequest
import com.ablil.springstarter.webapi.model.RegistrationRequest
import com.ablil.springstarter.webapi.model.ResetPasswordRequest
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Registration")
@SecurityRequirements
class RegistrationController(val registrationService: RegistrationService) : RegistrationApi {
    override fun confirmRegistration(token: String): ResponseEntity<Unit> {
        registrationService.confirmUserRegistration(token)
        // TODO: think about this, maybe we shouldn't do a redirection, but instead return 2xx code
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).headers {
            it.add("Location", "/register/confirm?confirmed=true")
        }.build()
    }

    override fun registerNewUser(body: RegistrationRequest): ResponseEntity<Unit> {
        val request = RegistrationDto(body.username, body.email, body.password)
        registrationService.registerNewUser(request)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    override fun sendPasswordResetLink(request: ForgetPasswordRequest): ResponseEntity<Unit> {
        registrationService.sendPasswordResetLink(request.email)
        return ResponseEntity.noContent().build()
    }

    override fun updateUserPassword(request: ResetPasswordRequest): ResponseEntity<Unit> {
        registrationService.resetUserPassword(request.token, request.password)
        return ResponseEntity.noContent().build()
    }
}

data class RegistrationDto(
    @NotBlank val username: String,
    @NotBlank val email: String,
    @NotBlank val password: String,
)
