package com.ablil.springstarter.miscllaneous

import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailClient(private val javaMailSender: JavaMailSender) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun sendEmail(to: String, subject: String, content: String) {
        javaMailSender.send(SimpleMailMessage().apply {
            setTo(to)
            setSubject(subject)
            text = content
        })
        logger.info("Sent email to $to with subject $subject")
    }
}