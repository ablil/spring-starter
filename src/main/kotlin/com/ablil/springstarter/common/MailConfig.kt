package com.ablil.springstarter.common

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties

@ConditionalOnProperty(name = ["spring.mail.host", "spring.mail.username", "spring.mail.password"])
@EnableConfigurationProperties(ApplicationProperties::class)
annotation class MailConfig()
