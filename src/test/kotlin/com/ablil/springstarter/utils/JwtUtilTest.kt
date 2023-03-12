package com.ablil.springstarter.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JwtUtilTest {

    @Test
    fun `should generate jwt token`() {
        val token = JwtUtil.generate("username")
        assertTrue(token.isNotEmpty())
    }

    @Test
    fun `should verify generated token`() {
        val token = JwtUtil.generate("username")
        assertTrue(JwtUtil.validate(token))
    }
}