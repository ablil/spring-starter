package com.ablil.springstarter.authentication.integration

import com.ablil.springstarter.common.BaseIntegrationTest
import com.ablil.springstarter.common.IntegrationTest
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.repositories.UserRepository
import com.ablil.springstarter.webapi.model.Token
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus

@IntegrationTest
class LoginIntegrationTest : BaseIntegrationTest() {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @BeforeEach
    fun beforeEach(): Unit = userRepository.truncate()

    @Test
    fun `login with username and password`() {
        val user = registerTestUser()

        val request = createLoginRequest(user.username, "supersecurepassword")
        val response = testRestTemplate.postForEntity("/api/auth/login", request, Token::class.java)

        assertAll(
            { assertEquals(HttpStatus.OK, response.statusCode) },
            { assertNotNull(response.body?.token) },
        )
    }

    @Test
    fun `login  with email and password`() {
        val user = registerTestUser()

        val request = createLoginRequest(user.email, "supersecurepassword")
        val response = testRestTemplate.postForEntity("/api/auth/login", request, Token::class.java)

        assertAll(
            { assertEquals(HttpStatus.OK, response.statusCode) },
            { assertNotNull(response.body?.token) },
        )
    }

    @Test
    fun `deny login given non-existing user`() {
        val request = createLoginRequest("nonexistinguser@example.com", "supersecurepassword")
        val response = testRestTemplate.postForEntity("/api/auth/login", request, Void::class.java)

        Assertions.assertNotEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `deny login given invalid credentials`() {
        val user = registerTestUser()

        val request = createLoginRequest(user.username, "invalidpassword")
        val response = testRestTemplate.postForEntity("/api/auth/login", request, Void::class.java)

        Assertions.assertNotEquals(HttpStatus.OK, response.statusCode)
    }

    private fun createLoginRequest(username: String, password: String): HttpEntity<String> {
        return createRequestWithJsonBody(
            """
            { "username": "$username", "password": "$password" }
            """.trimIndent(),
        )
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
