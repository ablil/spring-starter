package com.ablil.springstarter.authentication

import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class RegistrationController(
    private val registrationService: RegistrationService
) {

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "register new account")
    fun register(@RequestBody @Valid body: RegistrationRequest) {
        registrationService.register(body)
    }

    @GetMapping("register/confirm")
    fun confirmRegistration(@RequestParam("token") token: String): ResponseEntity<Unit> {
        registrationService.confirmRegistration(token)
        return ResponseEntity(
            HttpHeaders().apply { add("Location", "/confirmed") },
            HttpStatus.TEMPORARY_REDIRECT
        )
    }

}

data class RegistrationRequest(
    @Size(min = 6, message = "username must be at least 6 characters long")
    val username: String,

    @Size(min = 10, message = "password must be at least 10 characters long")
    val password: String,

    @Email
    val email: String,
)
