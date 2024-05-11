package com.ablil.springstarter

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
}
