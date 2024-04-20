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
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@IntegrationTest
class RegistrationIntegrationTest : BaseIntegrationTest() {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun beforeEach(): Unit = userRepository.truncate()

    @Nested
    inner class NewUserRegistrationTest {
        @Test
        fun `should register user successfully`() {
            mockMvc.post("/api/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content = buildRegistrationRequest(
                    RegistrationDto(
                        "johndoe",
                        "johndoe@example.com",
                        "supersecurepassword",
                    ),
                )
            }.andExpect { status { isCreated() } }
        }

        @Test
        fun `should NOT register an existing user`() {
            mockMvc.post("/api/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content = buildRegistrationRequest(
                    RegistrationDto(
                        "johndoe",
                        "johndoe@example.com",
                        "supersecurepassword",
                    ),
                )
            }.andExpect { status { isCreated() } }

            mockMvc.post("/api/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content = buildRegistrationRequest(
                    RegistrationDto(
                        "johndoe",
                        "johndoe@example.com",
                        "supersecurepassword",
                    ),
                )
            }.andExpect { status { isConflict() } }
        }

        @Test
        fun `should confirm user registration given valid token`() {
            mockMvc.post("/api/auth/register") {
                contentType = MediaType.APPLICATION_JSON
                content = buildRegistrationRequest(
                    RegistrationDto(
                        "johndoe",
                        "johndoe@example.com",
                        "supersecurepassword",
                    ),
                )
            }
            val token = userRepository.findByUsername("johndoe")?.token
                ?: IllegalStateException("User not found")

            mockMvc.get("/api/auth/register/confirm?token=$token")

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
            mockMvc.post("/api/auth/forget_password") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {"email": "johndoe@example.com"}
                    """.trimIndent()
            }.andExpect { status { isNoContent() } }
            val actualUser = userRepository.findByEmail("johndoe@example.com")

            assertEquals(AccountStatus.PASSWORD_RESET_IN_PROGRESS, actualUser?.status)
            Assertions.assertNotNull(actualUser?.token)
        }

        @Test
        fun `update user password given valid token`() {
            mockMvc.post("/api/auth/reset_password") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {"token": "token", "password": "mynewsupersecurepassword"}
                    """.trimIndent()
            }.andExpect { status { isNoContent() } }
            val actualUser = userRepository.findByEmail("johndoe@example.com")

            Assertions.assertEquals(AccountStatus.ACTIVE, actualUser?.status)
        }
    }

    private fun buildRegistrationRequest(dto: RegistrationDto): String {
        return """
            {"username": "${dto.username}", "email": "$dto.email", "password": "${dto.password}"}
            """.trimIndent()
    }
}
