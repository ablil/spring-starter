package com.ablil.springstarter.authentication

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class TokenNotFound(s: String) : RuntimeException(s)

@ResponseStatus(HttpStatus.CONFLICT)
class UserAlreadyExists(username: String) : RuntimeException(username)
