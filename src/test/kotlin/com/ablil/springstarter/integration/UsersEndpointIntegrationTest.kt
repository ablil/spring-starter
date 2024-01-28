package com.ablil.springstarter.integration

import com.ablil.springstarter.integration.common.BaseIntegrationTest
import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.entities.UserEntity
import com.ablil.springstarter.persistence.entities.UserRole
import com.ablil.springstarter.persistence.repositories.UserRepository
import com.ablil.springstarter.webapi.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class UsersEndpointIntegrationTest : BaseIntegrationTest() {
    @Autowired
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun beforeEach(): Unit = userRepository.truncate()

    @Test
    fun `get user by id given the caller is admin`() {
        registerAdminUser()
        val testUser = registerTestUser()

        val response = testRestTemplate.withBasicAuth("admin", "admin").getForEntity(
            "/api/users/${testUser.id}",
            User::class.java,
        )
        assertAll(
            { assertEquals(HttpStatus.OK, response.statusCode) },
            { assertEquals(testUser.id.toString(), response.body?.id) },
        )
    }

    @Test
    fun `forbid getting user by id given non-admin user as the caller`() {
        registerAdminUser()
        registerNonAdminUser()
        val testUser = registerTestUser()

        val response = testRestTemplate.withBasicAuth("nonadmin", "nonadmin").getForEntity(
            "/api/users/${testUser.id}",
            Void::class.java,
        )

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
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
