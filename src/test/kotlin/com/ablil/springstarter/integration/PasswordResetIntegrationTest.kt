package com.ablil.springstarter.integration

import com.ablil.springstarter.integration.common.BaseIntegrationTest
import com.ablil.springstarter.integration.common.IntegrationTest
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.repositories.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@IntegrationTest
class PasswordResetIntegrationTest : BaseIntegrationTest() {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @BeforeEach
    fun beforeEach(): Unit = userRepository.truncate()

    @Test
    fun `reset password`() {
        registerTestUser()

        val request =
            createRequestWithJsonBody(
                """
            {"email": "johndoe@example.com"}
                """.trimIndent(),
            )
        val response = testRestTemplate.postForEntity(
            "/api/auth/forget_password",
            request,
            Void::class.java,
        )
        val registeredUser = userRepository.findByEmail("johndoe@example.com")

        assertAll(
            { Assertions.assertEquals(HttpStatus.OK, response.statusCode) },
            { Assertions.assertNotNull(registeredUser?.token) },
            {
                Assertions.assertEquals(
                    AccountStatus.PASSWORD_RESET_IN_PROGRESS,
                    registeredUser?.status,
                )
            },
        )
    }

    @Test
    fun `reset password given valid token`() {
        registerTestUser(token = "testtoken")

        val request =
            createRequestWithJsonBody(
                """
            {"token": "testtoken", "password": "mynewsupersecurepassword"}
                """.trimIndent(),
            )
        val response = testRestTemplate.postForEntity(
            "/api/auth/reset_password",
            request,
            Void::class.java,
        )
        val registeredUser = userRepository.findByEmail("johndoe@example.com")

        assertAll(
            { Assertions.assertEquals(HttpStatus.OK, response.statusCode) },
            { Assertions.assertEquals(AccountStatus.ACTIVE, registeredUser?.status) },
            { Assertions.assertNull(registeredUser?.token) },
        )
    }

    @Test
    fun `should NOT reset password given invalid credentials`() {
        registerTestUser(token = "testtoken")

        val request =
            createRequestWithJsonBody(
                """
            {"token": "invalidtoken", "password": "mynewsupersecurepassword"}
                """.trimIndent(),
            )
        val response = testRestTemplate.postForEntity(
            "/api/auth/reset_password",
            request,
            Void::class.java,
        )
        val registeredUser = userRepository.findByEmail("johndoe@example.com")

        assertAll(
            { Assertions.assertNotEquals(HttpStatus.OK, response.statusCode) },
            {
                Assertions.assertEquals(
                    AccountStatus.PASSWORD_RESET_IN_PROGRESS,
                    registeredUser?.status,
                )
            },
            { Assertions.assertEquals("testtoken", registeredUser?.token) },
        )
    }

    fun registerTestUser(token: String? = null): UserEntity {
        return userRepository.save(
            UserEntity(
                id = null,
                username = "johndoe",
                email = "johndoe@example.com",
                password = "{noop}supersecurepassword",
                status = token?.let { AccountStatus.PASSWORD_RESET_IN_PROGRESS }
                    ?: AccountStatus.ACTIVE,
                token = token,
            ),
        )
    }
}
