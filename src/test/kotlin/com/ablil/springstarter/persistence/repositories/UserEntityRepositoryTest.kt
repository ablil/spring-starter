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
    fun `set created and updated date`() {
        userRepository.save(userEntity)
        val actual = userRepository.findByUsername(userEntity.username)
        assertAll(
            "Auditing attributs",
            { assertNotNull(actual?.createdAt) },
            { assertNotNull(actual?.updatedAt) },
        )
    }

    @Test
    fun `find user by username or email`() {
        userRepository.save(userEntity)
        assertAll(
            "fetch user from database",
            { assertNotNull(userRepository.findByUsernameOrEmail("joedoe", null)) },
            { assertNotNull(userRepository.findByUsernameOrEmail(null, "joedoe@example.com")) },
        )
    }

    @Test
    fun `update status and token by email`() {
        userRepository.save(userEntity)
        userRepository.updateTokenAndStatus("mytoken", AccountStatus.ACTIVE, userEntity.email)

        val actual = userRepository.findByEmail(userEntity.email)
        assertAll(
            { assertEquals("mytoken", actual?.token) },
            { assertEquals(AccountStatus.ACTIVE, actual?.status) },
        )
    }

    @Test
    fun `update password by email`() {
        userRepository.save(userEntity)
        val updated = userRepository.resetPassword("newsupersecurepassword", userEntity.email)
        assertEquals(1, updated)
    }

    @Test
    @WithMockUser(username = "johndoe", roles = ["USER"])
    fun `should set created by and updated by attributes`() {
        userRepository.save(userEntity)
        val actual = userRepository.findByUsername(userEntity.username)
        assertAll(
            "Auditing attributes",
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
