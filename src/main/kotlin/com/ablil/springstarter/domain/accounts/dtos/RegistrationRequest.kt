package com.ablil.springstarter.domain.accounts.dtos

import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "Registration request")
data class RegistrationRequest(
    val username: String,
    val password: String,
    val firstName: String?,
    val lastName: String?,
) {
    companion object {
        fun withUsernameAndPassword(username: String, password: String) = RegistrationRequest(
            username = username,
            password = password,
            firstName = null,
            lastName = null,
        )
    }
}