package com.ablil.springstarter.authentication

import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
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

}
data class RegistrationRequest(
    @Size(min = 6, message = "username must be at least 6 characters long")
    val username: String,
    @Size(min = 10, message = "password must be at least 10 characters long")
    val password: String,
)
