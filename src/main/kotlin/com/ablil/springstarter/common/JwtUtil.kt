package com.ablil.springstarter.common

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import java.time.Duration
import java.time.Instant

class JwtUtil {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private const val ISSUER = "myapp"
        private val ALGORITHM = Algorithm.HMAC256(ISSUER)
        private val verifier = JWT.require(ALGORITHM).withIssuer(ISSUER).build()
        private val tokenDuration = Instant.now().plus(
            Duration.ofSeconds(3600 * 4),
        )

        @JvmStatic
        fun generate(principal: String): String = JWT.create()
            .withIssuer(ISSUER)
            .withExpiresAt(tokenDuration)
            .withClaim("principal", principal)
            .sign(ALGORITHM)

        @JvmStatic
        fun isValid(token: String): Boolean = try {
            verifier.verify(token)
            true
        } catch (e: JWTVerificationException) {
            logger.warn("Failed to verify token $token")
            false
        }

        @JvmStatic
        fun extractClaim(token: String, claim: String): String = try {
            verifier.verify(token).getClaim(claim).asString()
        } catch (e: JWTVerificationException) {
            throw BadCredentialsException("Could NOT extract claim $claim from token $token")
        }
    }
}
