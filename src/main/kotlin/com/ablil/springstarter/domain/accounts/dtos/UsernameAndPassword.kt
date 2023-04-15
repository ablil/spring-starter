package com.ablil.springstarter.domain.accounts.dtos

import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "Login request")
data class UsernameAndPassword(val username: String, val password: String)
