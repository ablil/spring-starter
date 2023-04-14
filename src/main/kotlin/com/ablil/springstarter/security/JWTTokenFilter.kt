package com.ablil.springstarter.security

import com.ablil.springstarter.utils.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JWTTokenFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        filterChain.doFilter(request, response)
    }

    private fun authenticate(token: String) {
        val authentication = UsernamePasswordAuthenticationToken.authenticated(token, token, listOf())
        val emptyContext = SecurityContextHolder.createEmptyContext()
        emptyContext.authentication = authentication
        SecurityContextHolder.setContext(emptyContext)
    }

}