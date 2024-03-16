package com.ablil.springstarter.security

import com.ablil.springstarter.miscllaneous.JwtUtil
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Component

@Component
class AuthenticationTokenProvider(
    val userDetailsService: UserDetailsService,
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication? {
        if (!isBearerToken(authentication)) return null // let other providers handle this
        val principal = validateAndExtractPrincipal(authentication)
        val user = fetchUser(principal)
        return PreAuthenticatedAuthenticationToken(user, authentication.principal, user.authorities)
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return authentication == PreAuthenticatedAuthenticationToken::class.java
    }

    private fun fetchUser(username: String): UserDetails {
        val user = userDetailsService.loadUserByUsername(username)

        if (!user.isEnabled) {
            throw DisabledException("User account $username is disabled")
        }
        if (!user.isAccountNonLocked) {
            throw LockedException("User account $username is locked")
        }
        if (!user.isAccountNonExpired) {
            throw AccountExpiredException("User account $username is expired")
        }
        return user
    }

    private fun validateAndExtractPrincipal(authentication: Authentication): String {
        val token = authentication.principal.toString().replace("Bearer ", "")
        if (!JwtUtil.isValid(token)) {
            throw BadCredentialsException("Invalid token $token")
        }
        return JwtUtil.extractClaim(token, "principal")
    }

    private fun isBearerToken(authentication: Authentication): Boolean =
        authentication.principal.toString().startsWith("Bearer ")
}
