package com.ablil.springstarter.authentication

import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerTest(
    @Autowired val mockMvc: MockMvc,
    @MockkBean(relaxed = true) @Autowired val registrationService: RegistrationService
) {

    @Test
    fun `should return 200 given valid registration request`() {
        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                { "username": "joedoe", "password": "supersecurepassword", "email": "joedoe@example.com" }
            """.trimIndent()
        }.andExpect { status { isCreated() } }
    }

    @Test
    fun `should redirect user given valid registration confirmation token`() {
        mockMvc.get("/auth/register/confirm?token=mytoken").andExpect {
            status { isTemporaryRedirect() }
            header { string("Location", "/confirmed") }
        }
    }
}
