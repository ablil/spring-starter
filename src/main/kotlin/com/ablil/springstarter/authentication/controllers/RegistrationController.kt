package com.ablil.springstarter.authentication.controllers

import com.ablil.springstarter.authentication.services.RegistrationService
import com.ablil.springstarter.webapi.api.RegistrationApi
import com.ablil.springstarter.webapi.model.RegistrationRequest
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@Tag(name = "Registration")
class RegistrationController(
    private val registrationService: RegistrationService,
) : RegistrationApi {
    override fun confirmRegistration(token: String): ResponseEntity<Unit> {
        registrationService.confirmRegistration(token)
        // TODO: think about this, maybe we shouldn't do a redirection, but instead return 2xx code
        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).headers {
            it.add("Location", "/register/confirm?confirmed=true")
        }.build()
    }

    override fun registerUser(registrationRequest: RegistrationRequest): ResponseEntity<Unit> {
        val registeredUser = registrationService.register(registrationRequest)
        return ResponseEntity.created(URI("/api/users/${registeredUser.id}")).build()
    }
}
