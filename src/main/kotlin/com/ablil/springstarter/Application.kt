package com.ablil.springstarter

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(
    info = Info(title = "Spring boot API Starter", description = "API specification")
)
@SecurityScheme(
    name = "Bearer token", type = SecuritySchemeType.HTTP, bearerFormat = "json web token", scheme = "bearer"
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
