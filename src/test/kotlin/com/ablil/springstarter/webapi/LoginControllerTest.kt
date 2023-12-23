package com.ablil.springstarter.webapi

import com.ablil.springstarter.common.InvalidCredentials
import com.ablil.springstarter.service.LoginService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
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
    fun `should return jwt token given valid login credentials`() {
        Mockito.`when`(loginService.login(LoginCredentials("joedoe", "supersecurepassword"))).thenReturn("token")

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"usernameOrEmail": "joedoe", "password": "supersecurepassword"}
            """.trimIndent()
        }.andExpectAll {
            status { isOk() }
            jsonPath("$.token") { value("token") }
        }
    }

    @Test
    fun `should return 403 given invalid credentials`() {
        Mockito.`when`(loginService.login(LoginCredentials("joedoe", "supersecurepassword")))
            .thenThrow(InvalidCredentials())

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"usernameOrEmail": "joedoe", "password": "supersecurepassword"}
            """.trimIndent()
        }.andExpectAll {
            status { isForbidden() }
        }
    }
}
