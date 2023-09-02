package com.ablil.springstarter.miscllaneous

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import java.time.Duration
import java.time.Instant
import org.slf4j.LoggerFactory


class JwtUtil {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private const val ISSUER = "myapp";
        private val ALGORITHM = Algorithm.HMAC256(ISSUER)
        private val verifier = JWT.require(ALGORITHM).withIssuer(ISSUER).build()

        @JvmStatic
        fun generate(principal: String): String = JWT.create()
            .withIssuer(ISSUER)
            .withExpiresAt(Instant.now().plus(Duration.ofHours(3)))
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
        fun extractClaim(token: String, claim: String): String =
            verifier.verify(token).getClaim(claim).asString()
    }

}