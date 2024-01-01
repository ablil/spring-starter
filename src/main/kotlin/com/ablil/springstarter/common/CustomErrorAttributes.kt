package com.ablil.springstarter.common

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.context.request.WebRequest

/**
 * Add error code to error response and remove some default attributes
 */
@Component
class CustomErrorAttributes : DefaultErrorAttributes() {
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
