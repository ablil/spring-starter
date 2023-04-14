package com.ablil.springstarter.domain.accounts.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class InvalidCredentials: RuntimeException() {
}