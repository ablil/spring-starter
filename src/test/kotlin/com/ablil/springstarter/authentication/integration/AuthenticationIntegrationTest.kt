package com.ablil.springstarter.authentication.integration

import com.ablil.springstarter.common.BaseIntegrationTest
import com.ablil.springstarter.common.IntegrationTest
import com.ablil.springstarter.common.JwtUtil
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.repositories.UserRepository
import com.ablil.springstarter.webapi.model.Token
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@IntegrationTest
class AuthenticationIntegrationTest : BaseIntegrationTest() {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @BeforeEach
    fun beforeEach(): Unit = userRepository.truncate()

    @Test
    fun `authentication successfully with username and password`() {
        val user = registerTestUser()
        val response = attemptAuthentication(
            user.username,
            user.password.replace("{noop}", ""),
            Token::class.java,
        )

        assertAll(
            { assertEquals(HttpStatus.OK, response.statusCode) },
            { assertTrue(JwtUtil.isValid(requireNotNull(response.body?.token))) },
        )
    }

    @Test
    fun `authenticate successfully with email and password`() {
        val user = registerTestUser()
        val response = attemptAuthentication(
            user.email,
            user.password.replace("{noop}", ""),
            Token::class.java,
        )

        assertAll(
            { assertEquals(HttpStatus.OK, response.statusCode) },
            { assertTrue(JwtUtil.isValid(requireNotNull(response.body?.token))) },
        )
    }

    @Test
    fun `forbid authentication for non existing users`() {
        val response = attemptAuthentication("nonexisting", "invalidpassword", Void::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    fun `deny login given invalid credentials`() {
        val user = registerTestUser()
        val response = attemptAuthentication(user.username, "invalidpassword", Void::class.java)
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    private fun <T> attemptAuthentication(
        username: String,
        password: String,
        clazz: Class<T>,
    ): ResponseEntity<T> {
        val request = createRequestWithJsonBody(
            """
            { "username": "$username", "password": "$password" }
            """.trimIndent(),
        )
        return testRestTemplate.postForEntity("/api/auth/login", request, clazz)
    }

    private fun registerTestUser(): UserEntity {
        return userRepository.save(
            UserEntity(
                id = null,
                username = "testuser",
                email = "example@exmaple.com",
                password = "{noop}supersecurepassword",
                status = AccountStatus.ACTIVE,
            ),
        )
    }
}
