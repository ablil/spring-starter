package com.ablil.springstarter.webapi

import com.ablil.springstarter.service.LoginService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(PasswordResetController::class)
@AutoConfigureMockMvc(addFilters = false)
class PasswordResetControllerTest(
    @Autowired val mockMvc: MockMvc,
    @MockBean @Autowired val loginService: LoginService,
) {

    @Test
    fun `return 200 given valid reset password request`() {
        mockMvc.post("/api/auth/reset_password") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"token": "token", "password": "supersecurepassword"}
            """.trimIndent()
        }.andExpect { status { isOk() } }
    }

    @Test
    fun `return 200 given forget password request`() {
        mockMvc.post("/api/auth/forget_password") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"email": "joedoe@example.com"}
            """.trimIndent()
        }.andExpect { status { isOk() } }
    }
}
