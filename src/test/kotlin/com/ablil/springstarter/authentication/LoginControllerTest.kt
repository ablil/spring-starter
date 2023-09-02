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
    @Autowired @MockkBean(relaxed = true) val loginService: LoginService,
) {
    @Test
    fun `should return token given valid login credentials`() {
        every { loginService.login(any()) } returns "token"

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"usernameOrEmail": "joedoe", "password": "supersecurepassword"}
            """.trimIndent()
        }.andExpectAll {
            status { isOk() }
            content { jsonPath("$.token") { exists() } }
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


}