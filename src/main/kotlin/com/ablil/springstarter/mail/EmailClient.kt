package com.ablil.springstarter.mail

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["spring.mail.host"])
class EmailClient(private val javaMailSender: JavaMailSender) {
    fun send(to: String, subject: String, content: String) {
        val mail = SimpleMailMessage().apply {
            setTo(to)
            setSubject(subject)
            text = content
        }
        javaMailSender.send(mail)
        logger.debug("Email sent to $to with subject $subject")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}
