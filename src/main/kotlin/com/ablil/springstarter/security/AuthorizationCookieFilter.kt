package com.ablil.springstarter.security

import com.ablil.springstarter.miscllaneous.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AuthorizationCookieFilter(
    private val userDetailsService: UserDetailsService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (!Config.publicEndpoints.contains(request.servletPath)) {
            request.cookies?.firstOrNull { it.name == "jwt" }?.takeIf { JwtUtil.isValid(it.value) }
                ?.also { authenticate(it.value) }
        }

        filterChain.doFilter(request, response)
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
