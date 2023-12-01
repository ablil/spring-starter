package com.ablil.springstarter

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@OpenAPIDefinition(
    info = Info(title = "Spring boot API Starter", description = "API specification", version = "0.0.1"),
    security = [SecurityRequirement(name = "jwt")],
)
@SecurityScheme(
    name = "jwt",
    `in` = SecuritySchemeIn.COOKIE,
    type = SecuritySchemeType.APIKEY,
    scheme = "bearer",
)
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
