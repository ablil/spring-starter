package com.ablil.springstarter.integration

import com.ablil.springstarter.persistence.common.BaseIntegrationTest
import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.entities.UserRole
import com.ablil.springstarter.persistence.repositories.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class RegistrationIntegrationTest : BaseIntegrationTest() {
    @Autowired
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun beforeEach() {
        userRepository.deleteAll()
    }

    @Test
    fun `should register user successfully`() {
        val request = createRequestWithJsonBody(registrationRequest)

        val response = testRestTemplate.postForEntity<Any>("/api/auth/register", request, null)
        val savedUser = userRepository.findByUsername("testuser")

        assertAll(
            { Assertions.assertEquals(HttpStatus.CREATED, response.statusCode) },
            { Assertions.assertEquals("testuser", savedUser?.username) },
            { Assertions.assertEquals("testuser@example.com", savedUser?.email) },
            { Assertions.assertEquals(UserRole.USER, savedUser?.role) },
            { Assertions.assertEquals(AccountStatus.INACTIVE, savedUser?.status) },
            { Assertions.assertNotNull(savedUser?.token) },
        )
    }

    @Test
    fun `should NOT register an existing user`() {
        val request = createRequestWithJsonBody(registrationRequest)

        testRestTemplate.postForEntity<Any>("/api/auth/register", request, null)
        val response = testRestTemplate.postForEntity<Any>("/api/auth/register", request, null)

        Assertions.assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }

    @Test
    fun `should confirm user registration given valid token`() {
        val request = createRequestWithJsonBody(registrationRequest)
        testRestTemplate.postForEntity<Any>("/api/auth/register", request, null)
        val registeredUserToken = userRepository.findByUsername("testuser")?.token

        testRestTemplate.getForEntity(
            "/api/auth/register/confirm?token=$registeredUserToken",
            Void::class.java,
        )

        val registeredUser = userRepository.findByUsername("testuser")
        assertAll(
            { Assertions.assertNull(registeredUser?.token) },
            { Assertions.assertEquals(AccountStatus.ACTIVE, registeredUser?.status) },
        )
    }

    companion object {
        private val registrationRequest = """
            {"username": "testuser", "email": "testuser@example.com", "password": "supersecurepassword"}
        """.trimIndent()
    }
}