package com.ablil.springstarter

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@OpenAPIDefinition(
    info = Info(title = "Spring boot API Starter", description = "API specification", version = "0.0.1"),
    security = [SecurityRequirement(name = "Authorization")],
)
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@EnableJpaAuditing
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
