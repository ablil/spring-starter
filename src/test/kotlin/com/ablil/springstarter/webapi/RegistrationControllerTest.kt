package com.ablil.springstarter.webapi

import com.ablil.springstarter.authentication.RegistrationService
import com.ablil.springstarter.miscllaneous.TestUser
import com.ablil.springstarter.persistence.entities.User
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(RegistrationController::class)
@Disabled // TODO: should be an integration test
class RegistrationControllerTest(
        @Autowired val mockMvc: MockMvc,
        @MockBean @Autowired val registrationService: RegistrationService,
) {

    @Test
    fun `should return 200 given valid registration request`() {
        Mockito.`when`(registrationService.register(Mockito.any())).thenReturn(user)
        mockMvc.post("/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                { "username": "joedoe", "password": "supersecurepassword", "email": "joedoe@example.com" }
            """.trimIndent()
        }.andExpect { status { isCreated() } }
    }

    @Test
    fun `should redirect user given valid registration confirmation token`() {
        Mockito.`when`(registrationService.confirmRegistration("mytoken")).thenReturn(Unit)
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
