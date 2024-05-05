package com.ablil.springstarter

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.request.WebRequest

@Configuration
class ApplicationConfig {
    @Bean
    fun errorAttributes(): ErrorAttributes = object : DefaultErrorAttributes() {
        override fun getErrorAttributes(
            webRequest: WebRequest?,
            options: ErrorAttributeOptions?,
        ): MutableMap<String, Any> {
            val exception = getError(webRequest)
            return super.getErrorAttributes(webRequest, options).also {
                arrayOf("timestamp", "status", "path").forEach { attr -> it.remove(attr) }
                it["message"] = exception?.message ?: "Unexpected error"
                it["code"] = if (exception is DefaultBusinessError) exception.code else "error-000"
            }
        }
    }

    @Bean
    fun getApiInfo(): Info = Info().title("API Specification").version("0.0.1")

    @Bean
    fun openApiProperties(info: Info): OpenAPI = OpenAPI().info(info).externalDocs(
        ExternalDocumentation().description("Github repository").url(
            "https://github" +
                ".com/ablil/spring-starter.git",
        ),
    ).components(
        Components().addSecuritySchemes(
            "bearerAuth",
            SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
                .description("Api endpoints are security with bearer token (jwt)"),
        ),
    ).addSecurityItem(SecurityRequirement().addList("bearerAuth").addList("basicAuth"))
}
