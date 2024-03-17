package com.ablil.springstarter.authentication.controllers

import com.ablil.springstarter.InvalidCredentials
import com.ablil.springstarter.authentication.services.LoginService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(LoginController::class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest(
    @Autowired val mockMvc: MockMvc,
    @MockBean @Autowired val loginService: LoginService,
) {
    @Test
    fun `return jwt token given valid login credentials`() {
        whenever(loginService.login(LoginCredentials("joedoe", "supersecurepassword")))
            .thenReturn("token")

        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"username": "joedoe", "password": "supersecurepassword"}
            """.trimIndent()
        }.andExpectAll {
            status { isOk() }
            jsonPath("$.token") { value("token") }
        }
    }

    @Test
    fun `should return 403 given invalid credentials`() {
        whenever(loginService.login(LoginCredentials("joedoe", "supersecurepassword")))
            .thenThrow(InvalidCredentials())

        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"username": "joedoe", "password": "supersecurepassword"}
            """.trimIndent()
        }.andExpectAll {
            status { isForbidden() }
        }
    }
}
