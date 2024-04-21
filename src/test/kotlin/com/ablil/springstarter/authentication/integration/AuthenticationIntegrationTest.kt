package com.ablil.springstarter.authentication.integration

import com.ablil.springstarter.common.BaseIntegrationTest
import com.ablil.springstarter.common.IntegrationTest
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.repositories.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

const val LOGIN_ENDPOINT = "/api/auth/login"

@IntegrationTest
class AuthenticationIntegrationTest : BaseIntegrationTest() {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun beforeEach(): Unit = userRepository.truncate()

    @ParameterizedTest
    @ValueSource(strings = ["testuser", "example@exmaple.com"])
    fun `authentication successfully with valid credentials `(principal: String) {
        val user = registerTestUser()
        mockMvc.post(LOGIN_ENDPOINT) {
            contentType = MediaType.APPLICATION_JSON
            content = credentials(principal, user.password.replace("{noop}", ""))
        }.andExpectAll {
            status { isOk() }
            jsonPath("$.token") { exists() }
        }
    }

    @ParameterizedTest
    @CsvSource(*["nonexistinguser,invalidpassword", "testuser,invalidpassword"])
    fun `forbid authentication with invalid credentials`(principal: String, password: String) {
        val user = registerTestUser()
        mockMvc.post(LOGIN_ENDPOINT) {
            contentType = MediaType.APPLICATION_JSON
            content = credentials(principal, password)
        }.andExpect { status { isUnauthorized() } }
    }

    private fun credentials(username: String, password: String) =
        """
        {
            "username": "$username",
            "password": "$password"
        }
        """.trimIndent()

    private fun registerTestUser(): UserEntity {
        return userRepository.save(
            UserEntity(
                id = null,
                username = "testuser",
                email = "example@exmaple.com",
                password = "{noop}supersecurepassword",
                status = AccountStatus.ACTIVE,
            ),
        )
    }
}
