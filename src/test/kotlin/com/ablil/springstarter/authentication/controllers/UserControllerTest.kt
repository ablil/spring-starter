package com.ablil.springstarter.authentication.controllers

import com.ablil.springstarter.users.controllers.UserController
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.services.UserService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(UserController::class)
@AutoConfigureMockMvc
class UserControllerTest(
    @Autowired val mockMvc: MockMvc,
    @MockBean @Autowired val userService: UserService,
) {
    @Test
    @WithMockUser(username = "admin", password = "admin", roles = ["ADMIN"])
    fun `should return given users given id`() {
        whenever(userService.findById(any())).thenReturn(userEntity)

        mockMvc.get("/api/users/1").andExpect {
            status { isOk() }
            jsonPath("$.id") { exists() }
            jsonPath("$.username") { value(userEntity.username) }
            jsonPath("$.email") { userEntity.email }
            jsonPath("$.status") { value(userEntity.status.name) }
            jsonPath("$.role") { value(userEntity.role.name) }
        }
    }

    @Test
    fun `non-admin users should not be able to fetch users by id`() {
        mockMvc.get("/api/users/1").andExpect {
            status { isUnauthorized() }
        }
    }

    companion object {
        val userEntity = UserEntity(
            id = null,
            username = "joedoe",
            email = "johndoe@example.com",
            status = AccountStatus.ACTIVE,
            password = "supersecurepassword",
        )
    }
}
