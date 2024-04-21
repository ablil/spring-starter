package com.ablil.springstarter.authentication.integration

import com.ablil.springstarter.common.BaseIntegrationTest
import com.ablil.springstarter.common.IntegrationTest
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.entities.UserRole
import com.ablil.springstarter.users.repositories.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@IntegrationTest
class UsersEndpointIntegrationTest : BaseIntegrationTest() {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun beforeEach(): Unit = userRepository.truncate()

    @Test
    @WithMockUser(username = "admin", authorities = ["ADMIN"])
    fun `get user by id given the caller is admin`() {
        registerAdminUser()
        val testUser = registerTestUser()

        mockMvc.get("/api/users/${testUser.id}")
            .andExpectAll {
                status { isOk() }
                jsonPath("$.id") { value(testUser.id) }
            }
    }

    fun registerAdminUser(): UserEntity = userRepository.save(
        UserEntity(
            id = null,
            username = "admin",
            password = "{noop}admin",
            email = "admin@example.com",
            role = UserRole.ADMIN,
            status = AccountStatus.ACTIVE,
        ),
    )

    fun registerNonAdminUser(): UserEntity = userRepository.save(
        UserEntity(
            id = null,
            username = "nonadmin",
            password = "{noop}nonadmin",
            email = "nonadmin@example.com",
        ),
    )

    fun registerTestUser(): UserEntity = userRepository.save(
        UserEntity(
            id = null,
            username = "testuser",
            password = "supersecurepassword",
            email = "test@example.com",
        ),
    )
}
