package com.ablil.springstarter.authentication.controllers

import com.ablil.springstarter.authentication.services.RegistrationService
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.webapi.model.RegistrationRequest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
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
    fun `should return 201 given valid registration request`() {
        whenever(
            registrationService.register(
                RegistrationRequest(
                    username = "joedoe",
                    password = "supersecurepassword",
                    email = "joedoe@example.com",
                ),
            ),
        ).thenReturn(userEntity)

        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content =
                """
                { "username": "joedoe", "password": "supersecurepassword", "email": "joedoe@example.com" }
                """.trimIndent()
        }.andExpectAll {
            status { isCreated() }
            header { string("Location", "/api/users/${userEntity.id}") }
        }
    }

    @Test
    fun `should redirect user given valid registration confirmation token`() {
        mockMvc.get("/api/auth/register/confirm?token=mytoken").andExpect {
            status { isTemporaryRedirect() }
            header { string("Location", "/register/confirm?confirmed=true") }
        }
    }

    companion object {
        val userEntity = UserEntity(
            id = null,
            username = "joedoe",
            email = "joedoe@example.com",
            password = "supersecurepassword",
        )
    }
}
