package com.ablil.springstarter.miscllaneous

import com.ablil.springstarter.common.MailConfig
import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@MailConfig
@Component
class EmailClient(private val javaMailSender: JavaMailSender) {
    fun sendEmail(to: String, subject: String, content: String) {
        val mail = SimpleMailMessage().apply {
            setTo(to)
            setSubject(subject)
            text = content
        }
        javaMailSender.send(mail)
        logger.info("Sent email to $to with subject $subject")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}
