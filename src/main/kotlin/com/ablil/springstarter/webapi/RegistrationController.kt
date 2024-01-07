package com.ablil.springstarter.webapi

import com.ablil.springstarter.service.RegistrationService
import com.ablil.springstarter.webapi.api.RegistrationApi
import com.ablil.springstarter.webapi.model.RegistrationRequest
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

    override fun registerUser(registrationRequest: RegistrationRequest): ResponseEntity<Unit> {
        val registeredUser = registrationService.register(registrationRequest)
        return ResponseEntity.created(URI("/api/users/${registeredUser.id}")).build()
    }
}
