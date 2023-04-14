package com.ablil.springstarter.security

import com.ablil.springstarter.domain.accounts.AccountRepository
import com.ablil.springstarter.utils.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JWTTokenFilter(
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        request.getHeader("Authorization")
            ?.takeIf { it.startsWith("Bearer") }
            ?.substring("Bearer ".length)
            ?.takeIf(JwtUtil::isValid)
            ?.also(::authenticate)

        filterChain.doFilter(request, response)
    }

    private fun authenticate(token: String) {
        val username = JwtUtil.extractClaim(token, "principal")
        try {
            userDetailsService.loadUserByUsername(username)
                ?.let { UsernamePasswordAuthenticationToken(it, token, emptyList()) }
                ?.also {
                    val emptyContext = SecurityContextHolder.createEmptyContext().apply { authentication = it }
                    SecurityContextHolder.setContext(emptyContext)
                }
        } catch (e: UsernameNotFoundException) {
            logger.warn("Failed to authenticate with token $token, user $username does NOT exists !")
        }
    }

}