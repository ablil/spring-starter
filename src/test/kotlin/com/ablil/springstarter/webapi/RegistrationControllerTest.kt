package com.ablil.springstarter.webapi

import com.ablil.springstarter.persistence.entities.User
import com.ablil.springstarter.service.RegistrationService
import org.junit.jupiter.api.Test
import org.mockito.Mockito
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
class RegistrationControllerTest(
    @Autowired val mockMvc: MockMvc,
    @MockBean @Autowired val registrationService: RegistrationService,
) {

    @Test
    fun `should return 200 given valid registration request`() {
        Mockito.`when`(
            registrationService.register(
                RegistrationRequest(
                    username = "joedoe",
                    password = "supersecurepassword",
                    email = "joedoe@example.com"
                )
            )
        ).thenReturn(user)
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
            header { string("Location", "/register/confirm?confirmed=true") }
        }
    }

    companion object {
        val user = User(
            id = null,
            username = "joedoe",
            email = "joedoe@example.com",
            password = "supersecurepassword",
        )
    }
}
