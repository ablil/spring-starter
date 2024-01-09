package com.ablil.springstarter.integration

import com.ablil.springstarter.persistence.common.BaseIntegrationTest
import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.entities.UserEntity
import com.ablil.springstarter.persistence.repositories.UserRepository
import com.ablil.springstarter.webapi.model.Token
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus

class LoginIntegrationTest: BaseIntegrationTest() {

    @Autowired
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun beforeEach() {
        userRepository.deleteAll()
    }

    @Test
    fun `login successfully with username and password`() {
        val user = registerTestUser()

        val request = createLoginRequest(user.username, "supersecurepassword")
        val response = testRestTemplate.postForEntity("/api/auth/login", request, Token::class.java)

        assertAll(
                { Assertions.assertEquals(HttpStatus.OK, response.statusCode) },
                { Assertions.assertNotNull(response.body?.token) }
        )
    }

    @Test
    fun `login successfully with email and password`() {
        val user = registerTestUser()

        val request = createLoginRequest(user.email, "supersecurepassword")
        val response = testRestTemplate.postForEntity("/api/auth/login", request, Token::class.java)

        assertAll(
                { Assertions.assertEquals(HttpStatus.OK, response.statusCode) },
                { Assertions.assertNotNull(response.body?.token) }
        )

    }

    @Test
    fun `deny login for non-existing user`() {
        val request = createLoginRequest("nonexistinguser@example.com", "supersecurepassword")
        val response = testRestTemplate.postForEntity("/api/auth/login", request, Token::class.java)

        Assertions.assertNotEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `deny login for invalid credentials`() {
        val user = registerTestUser()

        val request = createLoginRequest(user.username, "invalidpassword")
        val response = testRestTemplate.postForEntity("/api/auth/login", request, Token::class.java)

        Assertions.assertNotEquals(HttpStatus.OK, response.statusCode)

    }

    private fun createLoginRequest(username: String, password: String): HttpEntity<String> {
        return createRequestWithJsonBody("""
            { "username": "$username", "password": "$password" }
        """.trimIndent())
    }

    private fun registerTestUser(): UserEntity {
        return userRepository.save(UserEntity(
                id = null,
                username = "testuser",
                email = "example@exmaple.com",
                password = "{noop}supersecurepassword",
                status = AccountStatus.ACTIVE
        ))
    }
}