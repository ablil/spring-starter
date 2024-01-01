package com.ablil.springstarter.webapi

import com.ablil.springstarter.service.LoginService
import com.ablil.springstarter.webapi.api.PasswordApi
import com.ablil.springstarter.webapi.model.ForgetPasswordRequest
import com.ablil.springstarter.webapi.model.ResetPasswordRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PasswordResetController(
    private val loginService: LoginService,
) : PasswordApi {
    override fun forgetPassword(
        forgetPasswordRequest: ForgetPasswordRequest,
    ): ResponseEntity<Unit> {
        loginService.forgetPassword(forgetPasswordRequest.email)
        return ResponseEntity.ok().build()
    }

    override fun resetPassword(resetPasswordRequest: ResetPasswordRequest): ResponseEntity<Unit> {
        loginService.resetPassword(resetPasswordRequest.token, resetPasswordRequest.password)
        return ResponseEntity.ok().build()
    }
}
