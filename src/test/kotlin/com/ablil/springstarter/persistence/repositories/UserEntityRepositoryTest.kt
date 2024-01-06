package com.ablil.springstarter.persistence.repositories

import com.ablil.springstarter.persistence.common.RepositoryTest
import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.entities.UserEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

@RepositoryTest
class UserEntityRepositoryTest(
    @Autowired val userRepository: UserRepository,
) {
    @BeforeEach
    fun setup(): Unit = userRepository.deleteAll()

    @Test
    fun `createdBy and updatedBy attributes are set`() {
        val actual = with(userRepository) {
            save(userEntity)
            findByUsername(userEntity.username)
        }

        assertAll(
            { assertNotNull(actual?.createdAt) },
            { assertNotNull(actual?.updatedAt) },
        )
    }

    @Test
    fun `find user by username or email`() {
        userRepository.save(userEntity)
        assertAll(
            { assertNotNull(userRepository.findByUsernameOrEmail("joedoe", null)) },
            { assertNotNull(userRepository.findByUsernameOrEmail(null, "joedoe@example.com")) },
        )
    }

    @Test
    fun `update status and token by email`() {
        val actual = with(userRepository) {
            save(userEntity)
            updateTokenAndStatus("mytoken", AccountStatus.ACTIVE, userEntity.email)
            findByEmail(userEntity.email)
        }

        assertAll(
            { assertEquals("mytoken", actual?.token) },
            { assertEquals(AccountStatus.ACTIVE, actual?.status) },
        )
    }

    @Test
    fun `update password by email`() {
        val updatedEntries = with(userRepository) {
            save(userEntity)
            resetPassword("newsupersecurepassword", userEntity.email)
        }
        assertEquals(1, updatedEntries)
    }

    @Test
    @WithMockUser(username = "johndoe", roles = ["USER"])
    fun `should set created by and updated by attributes`() {
        val actual = with(userRepository) {
            save(userEntity)
            findByUsername(userEntity.username)
        }

        assertAll(
            { assertEquals("johndoe", actual?.createdBy) },
            { assertEquals("johndoe", actual?.updatedBy) },
        )
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
