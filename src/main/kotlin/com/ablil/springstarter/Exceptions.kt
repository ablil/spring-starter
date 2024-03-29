package com.ablil.springstarter

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

abstract class DefaultBusinessError(
    val code: String,
    override val message: String,
) : RuntimeException(message)

@ResponseStatus(HttpStatus.NOT_FOUND)
class TokenNotFound(s: String) :
    DefaultBusinessError("auth-001", "token not found: $s")

@ResponseStatus(HttpStatus.CONFLICT)
class UserAlreadyExists(
    username: String,
) : DefaultBusinessError("auth-002", "user $username already exists")

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidCredentials(override val message: String) : DefaultBusinessError("auth-003", message)

@ResponseStatus(HttpStatus.FORBIDDEN)
class ResetPasswordError(msg: String) : DefaultBusinessError("auth-004", msg)
