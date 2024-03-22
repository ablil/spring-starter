package com.ablil.springstarter.authentication.controllers

import com.ablil.springstarter.UserAlreadyExists
import com.ablil.springstarter.authentication.services.RegistrationService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(RegistrationController::class)
@AutoConfigureMockMvc(addFilters = false)
class RegistrationControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var registrationService: RegistrationService

    @Test
    fun `return 201 given valid registration request`() {
        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content =
                """
                { "username": "joedoe", "password": "supersecurepassword", "email": "joedoe@example.com" }
                """.trimIndent()
        }.andExpectAll {
            status { isCreated() }
        }
    }

    @Test
    fun `return 409 given an existing registered user`() {
        whenever(
            registrationService.registerNewUser(
                RegistrationDto(
                    "joedoe",
                    "joedoe@example.com",
                    "supersecurepassword",
                ),
            ),
        ).thenThrow(UserAlreadyExists("joedoe"))

        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content =
                """
                { "username": "joedoe", "password": "supersecurepassword", "email": "joedoe@example.com" }
                """.trimIndent()
        }.andExpectAll {
            status { isConflict() }
        }
    }

    @Test
    fun `return 201 given valid password reset request`() {
        mockMvc.post("/api/auth/reset_password") {
            contentType = MediaType.APPLICATION_JSON
            content =
                """
                {"token": "token", "password": "supersecurepassword"}
                """.trimIndent()
        }.andExpect { status { isNoContent() } }
    }

    @Test
    fun `return 201 given forget password request`() {
        mockMvc.post("/api/auth/forget_password") {
            contentType = MediaType.APPLICATION_JSON
            content =
                """
                {"email": "joedoe@example.com"}
                """.trimIndent()
        }.andExpect { status { isNoContent() } }
    }

    @Test
    fun `should redirect user given valid registration confirmation token`() {
        mockMvc.get("/api/auth/register/confirm?token=mytoken").andExpect {
            status { isTemporaryRedirect() }
            header { string("Location", "/register/confirm?confirmed=true") }
        }
    }
}
