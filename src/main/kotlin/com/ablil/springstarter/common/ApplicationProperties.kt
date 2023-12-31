package com.ablil.springstarter.common

import jakarta.validation.constraints.NotBlank
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Configuration
@ConfigurationProperties(prefix = "app")
@Validated
class ApplicationProperties {
    @NotBlank
    lateinit var url: String
}
