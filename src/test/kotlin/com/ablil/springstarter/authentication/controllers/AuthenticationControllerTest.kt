package com.ablil.springstarter.authentication.controllers

import com.ablil.springstarter.InvalidCredentials
import com.ablil.springstarter.authentication.services.AuthenticationService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(AuthenticationController::class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var authenticationService: AuthenticationService

    @Test
    fun `return bearer token given valid login credentials`() {
        whenever(
            authenticationService.validateCredentialsAndGenerateToken(
                Credentials(
                    "joedoe",
                    "supersecurepassword",
                ),
            ),
        ).thenReturn("token")

        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content =
                """
                {"username": "joedoe", "password": "supersecurepassword"}
                """.trimIndent()
        }.andExpectAll {
            status { isOk() }
            jsonPath("$.token") { value("token") }
        }
    }

    @Test
    fun `return 401 given invalid credentials`() {
        whenever(
            authenticationService.validateCredentialsAndGenerateToken(
                Credentials(
                    "joedoe",
                    "supersecurepassword",
                ),
            ),
        ).thenThrow(InvalidCredentials("invalid credentials"))

        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content =
                """
                {"username": "joedoe", "password": "supersecurepassword"}
                """.trimIndent()
        }.andExpectAll {
            status { isUnauthorized() }
        }
    }
}
