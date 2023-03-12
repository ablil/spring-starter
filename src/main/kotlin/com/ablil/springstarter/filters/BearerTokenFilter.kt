package com.ablil.springstarter.filters

import com.ablil.springstarter.utils.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class BearerTokenFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header: String? = request.getHeader("Authorization")
        header?.takeIf { it.startsWith("Bearer") }
            ?.let { it.substring("Bearer ".length) }
            ?.takeIf { JwtUtil.validate(it) }
            ?.also { authenticate(it) }

        filterChain.doFilter(request, response)
    }

    private fun authenticate(token: String) {
        val authentication = UsernamePasswordAuthenticationToken.authenticated(token, token, listOf())
        val emptyContext = SecurityContextHolder.createEmptyContext()
        emptyContext.authentication = authentication
        SecurityContextHolder.setContext(emptyContext)
    }

}