package com.ablil.springstarter.service

import com.ablil.springstarter.common.ApplicationProperties
import com.ablil.springstarter.common.MailConfig
import com.ablil.springstarter.miscllaneous.EmailClient
import org.springframework.stereotype.Service

@Service
@MailConfig
class MailService(
    val applicationProperties: ApplicationProperties,
    val emailClient: EmailClient?,
) {
    fun resetPassword(to: String, token: String) {
        // This is a frontend endpoint that will be used to reset the password, the token should be sent from there to the backend
        val link = "${applicationProperties.url}/reset-password?token=$token"
        emailClient?.sendEmail(to, "RESET PASSWORD", link)
    }

    fun passwordHasBeenReset(to: String) {
        emailClient?.sendEmail(to, "PASSWORD RESET", "Your password has been reset")
    }

    fun confirmRegistration(to: String, token: String) {
        val link = "${applicationProperties.url}/auth/register/confirm?token=$token"
        emailClient?.sendEmail(to, "CONFIRM REGISTRATION", link)
    }
}
