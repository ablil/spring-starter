package com.ablil.springstarter.security.filters

import com.ablil.springstarter.miscllaneous.JwtUtil
import com.ablil.springstarter.security.SecurityConfig
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Extracts JWT from Authorization header and validate it
 * JWT
 */
@Component
class JsonWebTokenFilter(private val userDetailsService: UserDetailsService) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        request.getHeader("Authorization")?.takeIf { it.startsWith("Bearer ") }
            ?.substring(7)
            ?.takeIf { JwtUtil.isValid(it) }
            ?.also { authenticate(it) }
        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return SecurityConfig.publicEndpoints.contains(request.servletPath)
    }

    private fun authenticate(token: String) {
        val username = JwtUtil.extractClaim(token, "principal")
        try {
            userDetailsService.loadUserByUsername(username)
                ?.takeIf { it.isEnabled }
                ?.let { UsernamePasswordAuthenticationToken(it, token, emptyList()) }
                ?.also {
                    val emptyContext = SecurityContextHolder.createEmptyContext().apply { authentication = it }
                    SecurityContextHolder.setContext(emptyContext)
                }
        } catch (e: UsernameNotFoundException) {
            logger.error("Failed to set security context for $username given $token")
        }
    }
}
