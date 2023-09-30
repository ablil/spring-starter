package com.ablil.springstarter.authentication

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired
    @MockkBean(relaxed = true)
    val loginService: LoginService,
) {
    @Test
    fun `should return token in cookie given valid login credentials`() {
        every { loginService.login(any()) } returns "token"

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
    fun `should return 403 given invalid credentials`() {
        every { loginService.login(any()) } throws InvalidCredentials()

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
