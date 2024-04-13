package com.ablil.springstarter

import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.entities.UserRole
import com.ablil.springstarter.users.repositories.UserRepository
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.context.request.WebRequest

@Configuration
class ApplicationConfig {
    @Value("\${app.audit.password}")
    private lateinit var password: String

    @Bean
    @Profile("local")
    fun createAdminAccount(userRepository: UserRepository, passwordEncoder: PasswordEncoder) =
        CommandLineRunner {
            val admin = userRepository.findByUsername("admin")
            if (admin == null) {
                userRepository.save(
                    UserEntity(
                        id = null,
                        username = "admin",
                        password = passwordEncoder.encode(password),
                        email = "admin@app.com",
                        role = UserRole.ADMIN,
                        status = AccountStatus.ACTIVE,
                    ),
                )
                logger.info("Admin user created")
            }
        }

    @Bean
    @Profile("local")
    fun createTestUser(userRepository: UserRepository, passwordEncoder: PasswordEncoder) =
        CommandLineRunner {
            val existingUser = userRepository.findByUsername("johndoe")
            if (existingUser == null) {
                userRepository.save(
                    UserEntity(
                        id = null,
                        username = "johndoe",
                        password = passwordEncoder.encode("supersecurepassword"),
                        email = "johndoe@example.com",
                        role = UserRole.DEFAULT,
                        status = AccountStatus.ACTIVE,
                    ),
                )
                logger.info(
                    "Test user created successfully. username=johndoe, email=johndoe@example" +
                        ".com, password=supersecurepassword",
                )
            }
        }

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

    @Profile("local")
    @Bean
    fun getApiInfoForLocal(): Info = Info().title("API Specification").version("0.0.1")
        .description(
            """
            A test account has been created automatically with the following details
            <br />
            username=<b>johndoe</b>
            <br />
            email=<b>johndoe@example.com</b>
            <br />
            password=<b>supersecurepassword</b>
            """.trimIndent(),
        )

    @ConditionalOnMissingBean
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
        )
            .addSecuritySchemes(
                "basicAuth",
                SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")
                    .description("Technical endpoints are secured with basic auth"),
            ),
    ).addSecurityItem(SecurityRequirement().addList("bearerAuth").addList("basicAuth"))

    companion object {
        private val logger = LoggerFactory.getLogger(ApplicationConfig::class.java)
    }
}
