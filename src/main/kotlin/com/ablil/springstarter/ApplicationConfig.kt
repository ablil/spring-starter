package com.ablil.springstarter

import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.entities.UserRole
import com.ablil.springstarter.users.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
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
    fun init(userRepository: UserRepository, passwordEncoder: PasswordEncoder) = CommandLineRunner {
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
    fun errorAttributes(): ErrorAttributes = object : DefaultErrorAttributes() {
        override fun getErrorAttributes(
            webRequest: WebRequest?,
            options: ErrorAttributeOptions?,
        ): MutableMap<String, Any> {
            val exception = getError(webRequest)
            return super.getErrorAttributes(webRequest, options).also {
                arrayOf("timestamp", "status", "errors", "path").forEach { attr -> it.remove(attr) }
                it["message"] = if (exception is DefaultBusinessError) exception.code else "error-000"
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}
