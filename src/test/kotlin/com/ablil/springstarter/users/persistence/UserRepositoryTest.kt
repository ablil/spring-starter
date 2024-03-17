package com.ablil.springstarter.users.persistence

import com.ablil.springstarter.common.RepositoryTest
import com.ablil.springstarter.common.testdata.UserEntityFactory
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.entities.UserRole
import com.ablil.springstarter.users.repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

@RepositoryTest
class UserRepositoryTest(
    @Autowired val repository: UserRepository,
) {
    @BeforeEach
    fun setup(): Unit = repository.truncate()

    @Test
    fun `truncate table`() {
        repository.save(UserEntityFactory.newUser())
        repository.truncate()
        assertEquals(0, repository.count())
    }

    @Test
    @WithMockUser(username = "johndoe", roles = ["DEFAULT"])
    fun `user saved successfully`() {
        val user = repository.save(
            UserEntity(
                id = null,
                username = "johndoe",
                email = "johndoe@example.com",
                password = "supersecurepassword",
                role = UserRole.DEFAULT,
                status = AccountStatus.ACTIVE,
                token = "token",
            ),
        )

        assertAll(
            { assertNotNull(user.id) },
            { assertEquals("johndoe", user.username) },
            { assertEquals("johndoe@example.com", user.email) },
            { assertEquals("supersecurepassword", user.password) },
            { assertEquals(UserRole.DEFAULT, user.role) },
            { assertEquals(AccountStatus.ACTIVE, user.status) },
            { assertEquals("token", user.token) },
            { assertEquals("johndoe", user.createdBy) },
            { assertEquals("johndoe", user.updatedBy) },
            { assertNotNull(user.createdAt) },
            { assertNotNull(user.updatedAt) },
        )
    }

    @Test
    fun `find user by username or email`() {
        val saved = repository.save(UserEntityFactory.newUser())
        assertAll(
            { assertNotNull(repository.findByUsernameOrEmail(saved.username)) },
            { assertNotNull(repository.findByUsernameOrEmail(saved.email)) },
        )
    }

    @Test
    fun `update user status and token`() {
        val saved = repository.save(UserEntityFactory.newUser())
        repository.updateTokenAndStatus("token", AccountStatus.ACTIVE, saved.email)
        val actual = repository.findByEmail(saved.email)

        assertAll(
            { assertEquals("token", actual?.token) },
            { assertEquals(AccountStatus.ACTIVE, actual?.status) },
        )
    }

    @Test
    fun `update user password`() {
        val saved = repository.save(UserEntityFactory.newUser())
        val totalUpdated = repository.resetPassword("newsupersecurepassword", saved.email)
        val actual = repository.findByEmail(saved.email)
        assertEquals(1, totalUpdated)
    }
}
