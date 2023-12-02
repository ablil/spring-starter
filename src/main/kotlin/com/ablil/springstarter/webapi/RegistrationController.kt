package com.ablil.springstarter.webapi

import com.ablil.springstarter.service.RegistrationService
import com.ablil.springstarter.webapi.api.RegistrationApi
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class RegistrationController(
    private val registrationService: RegistrationService,
) : RegistrationApi {

    override fun confirmRegistration(token: String): ResponseEntity<Unit> {
        registrationService.confirmRegistration(token)
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).headers {
            it.add("Location", "/register/confirm?confirmed=true")
        }.build()
    }

    override fun registerUser(registrationRequest: com.ablil.springstarter.webapi.model.RegistrationRequest): ResponseEntity<Unit> {
        registrationService.register(
            RegistrationRequest(
                username = registrationRequest.username,
                password = registrationRequest.password,
                email = registrationRequest.email,
            )
        )
        return ResponseEntity.created(URI("/")).build()
    }
}

@Schema(name = "registration request")
data class RegistrationRequest(
    @Size(min = 6, message = "username must be at least 6 characters long")
    val username: String,

    @Size(min = 10, message = "password must be at least 10 characters long")
    val password: String,

    @Email
    val email: String,
)
