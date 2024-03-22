package com.ablil.springstarter.authentication.integration

import com.ablil.springstarter.authentication.controllers.RegistrationDto
import com.ablil.springstarter.common.BaseIntegrationTest
import com.ablil.springstarter.common.IntegrationTest
import com.ablil.springstarter.common.testdata.UserEntityFactory
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.repositories.UserRepository
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@IntegrationTest
class RegistrationIntegrationTest : BaseIntegrationTest() {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @BeforeEach
    fun beforeEach(): Unit = userRepository.truncate()

    @Nested
    inner class NewUserRegistrationTest {
        @Test
        fun `should register user successfully`() {
            val response = attemptToRegisterUser(
                RegistrationDto(
                    "johndoe",
                    "johndoe@example.com",
                    "supersecurepassword",
                ),
            )

            assertEquals(HttpStatus.CREATED, response.statusCode)
        }

        @Test
        fun `should NOT register an existing user`() {
            val firstTimeResponse = attemptToRegisterUser(
                RegistrationDto(
                    "johndoe",
                    "johndoe@example.com",
                    "supersecurepassword",
                ),
            )

            val secondTimeResponse = attemptToRegisterUser(
                RegistrationDto(
                    "johndoe",
                    "johndoe@example.com",
                    "supersecurepassword",
                ),
            )

            assertEquals(HttpStatus.CREATED, firstTimeResponse.statusCode)
            assertEquals(HttpStatus.CONFLICT, secondTimeResponse.statusCode)
        }

        @Test
        fun `should confirm user registration given valid token`() {
            attemptToRegisterUser(
                RegistrationDto(
                    "johndoe",
                    "johndoe@example.com",
                    "supersecurepassword",
                ),
            )
            val token = userRepository.findByUsername("johndoe")?.token
                ?: IllegalStateException("User not found")

            testRestTemplate.getForEntity(
                "/api/auth/register/confirm?token=$token",
                Void::class.java,
            )

            val actualUser = userRepository.findByUsername("johndoe")
            assertEquals(AccountStatus.ACTIVE, actualUser?.status)
            Assertions.assertNull(actualUser?.token)
        }
    }

    @Nested
    inner class PasswordResetTests {
        @BeforeEach
        fun setup() {
            userRepository.truncate()
            userRepository.save(
                UserEntityFactory.newUser().copy(
                    email = "johndoe@example.com",
                    status = AccountStatus.PASSWORD_RESET_IN_PROGRESS,
                    token = "token",
                ),
            )
        }

        @Test
        fun `trigger password reset process`() {
            val request = createRequestWithJsonBody(
                """
                {"email": "johndoe@example.com"}
                """.trimIndent(),
            )

            val response = testRestTemplate.postForEntity(
                "/api/auth/forget_password",
                request,
                Void::class.java,
            )
            val actualUser = userRepository.findByEmail("johndoe@example.com")

            assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
            assertEquals(AccountStatus.PASSWORD_RESET_IN_PROGRESS, actualUser?.status)
            Assertions.assertNotNull(actualUser?.token)
        }

        @Test
        fun `update user password given valid token`() {
            val request =
                createRequestWithJsonBody(
                    """
                    {"token": "token", "password": "mynewsupersecurepassword"}
                    """.trimIndent(),
                )
            val response = testRestTemplate.postForEntity(
                "/api/auth/reset_password",
                request,
                Void::class.java,
            )
            val actualUser = userRepository.findByEmail("johndoe@example.com")

            Assertions.assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
            Assertions.assertEquals(AccountStatus.ACTIVE, actualUser?.status)
        }
    }

    private fun attemptToRegisterUser(dto: RegistrationDto): ResponseEntity<Void> {
        val request = createRequestWithJsonBody(
            """
            {"username": "${dto.username}", "email": "$dto.email", "password": "${dto.password}"}
            """.trimIndent(),
        )

        return testRestTemplate.postForEntity("/api/auth/register", request, Void::class.java)
    }
}
