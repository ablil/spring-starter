package com.ablil.springstarter.mail

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MailService(
    @Value("\${app.url}") val host: String,
    val client: EmailClient?,
) {
    fun sendResetPasswordLink(to: String, token: String) {
        val link = "$host/reset-password?token=$token"
        client?.send(to, "RESET PASSWORD", link)
    }

    fun notifyUserWithPasswordChange(to: String) {
        client?.send(to, "PASSWORD RESET", "Your password has been reset")
    }

    fun confirmRegistration(to: String, token: String) {
        val link = "$host/auth/register/confirm?token=$token"
        client?.send(to, "CONFIRM REGISTRATION", link)
    }
}
