package com.ablil.springstarter.security.filters

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Logs incoming requests
 */
@Component
class LogRequestsFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val headers = request.headerNames.toList().joinToString(", ") { "$it=${request.getHeader(it)}" }
            val params = request.parameterMap.map { "${it.key}: ${it.value}" }.joinToString(", ")

            logger.info("Incoming ${request.method} to ${request.servletPath} from ${request.remoteHost}")
            logger.debug("Header: $headers")
            logger.debug("Params: ${params.takeIf { it.isNotBlank() } ?: "none"}")
        } catch (ex: Exception) {
            logger.warn("Failed to log request", ex)
        }
        doFilter(request, response, filterChain)
    }
}
