package com.ablil.springstarter.webapi

import com.ablil.springstarter.authentication.RegistrationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Users registrations")
@RestController
@RequestMapping("/auth")
class RegistrationController(
        private val registrationService: RegistrationService,
) {

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "register new user")
    fun register(
            @RequestBody @Valid
        body: RegistrationRequest,
    ) {
        registrationService.register(body)
    }

    @GetMapping("register/confirm")
    @Operation(
        summary = "confirm user registration",
        description = "Given valid token, redirect user after confirmation",
    )
    @ResponseStatus(HttpStatus.TEMPORARY_REDIRECT)
    fun confirmRegistration(@RequestParam("token") token: String): ResponseEntity<Unit> {
        registrationService.confirmRegistration(token)
        return ResponseEntity(
            HttpHeaders().apply { add("Location", "/register/confirm?confirmed=true") },
            HttpStatus.TEMPORARY_REDIRECT,
        )
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
