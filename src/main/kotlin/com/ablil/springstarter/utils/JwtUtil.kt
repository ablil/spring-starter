package com.ablil.springstarter.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException


class JwtUtil {
    companion object {
        private val key = "supersecretkey"

        fun generate(principal: String): String {
            return JWT.create().withSubject(principal).sign(Algorithm.HMAC256(key))
        }

        fun validate(token: String): Boolean {
            return try {
                JWT.require(Algorithm.HMAC256(key)).build().verify(token)
                true
            } catch (e: JWTVerificationException) {
                false
            }
        }
    }

}