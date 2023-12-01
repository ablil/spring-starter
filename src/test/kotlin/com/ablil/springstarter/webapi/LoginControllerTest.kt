package com.ablil.springstarter.webapi

import com.ablil.springstarter.common.InvalidCredentials
import com.ablil.springstarter.service.LoginService
import org.junit.jupiter.api.Disabled
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
    @Disabled // TODO: maybe should be an intergration test
    fun `should return token in cookie given valid login credentials`() {
        Mockito.`when`(loginService.login(Mockito.any())).thenReturn("token")

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"usernameOrEmail": "joedoe", "password": "supersecurepassword"}
            """.trimIndent()
        }.andExpectAll {
            status { isNoContent() }
            cookie { exists("jwt") }
        }
    }

    @Test
    @Disabled // TODO: should be an intergration test
    fun `should return 403 given invalid credentials`() {
        Mockito.`when`(loginService.login(Mockito.any())).thenThrow(InvalidCredentials())

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"usernameOrEmail": "joedoe", "password": "supersecurepassword"}
            """.trimIndent()
        }.andExpectAll {
            status { isForbidden() }
        }
    }

    @Test
    fun `return 200 given valid reset password request`() {
        mockMvc.post("/auth/reset_password") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"token": "token", "password": "supersecurepassword"}
            """.trimIndent()
        }.andExpect { status { isOk() } }
    }

    @Test
    fun `return 200 given forget password request`() {
        mockMvc.post("/auth/forget_password") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"email": "joedoe@example.com"}
            """.trimIndent()
        }.andExpect { status { isOk() } }
    }
}
