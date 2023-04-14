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
    fun `should extract principal claim`() {
        val token = JwtUtil.generate("username")
        assertEquals("username", JwtUtil.extractClaim(token, "principal"))
    }

    @Test
    fun `should verify generated token`() {
        val token = JwtUtil.generate("username")
        assertTrue(JwtUtil.isValid(token))
    }

    @Test
    fun `should not validate random token`() {
        assertFalse(JwtUtil.isValid("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"))
    }
}