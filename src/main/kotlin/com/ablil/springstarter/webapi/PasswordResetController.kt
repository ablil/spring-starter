package com.ablil.springstarter.webapi

import com.ablil.springstarter.service.LoginService
import com.ablil.springstarter.webapi.api.PasswordApi
import com.ablil.springstarter.webapi.model.AuthForgetPasswordPostRequest
import com.ablil.springstarter.webapi.model.AuthResetPasswordPostRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PasswordResetController(
    private val loginService: LoginService
) : PasswordApi {
    override fun authForgetPasswordPost(authForgetPasswordPostRequest: AuthForgetPasswordPostRequest): ResponseEntity<Unit> {
        loginService.forgetPassword(authForgetPasswordPostRequest.email)
        return ResponseEntity.ok().build()
    }

    override fun authResetPasswordPost(authResetPasswordPostRequest: AuthResetPasswordPostRequest): ResponseEntity<Unit> {
        loginService.resetPassword(authResetPasswordPostRequest.token, authResetPasswordPostRequest.password)
        return ResponseEntity.ok().build()
    }
}
