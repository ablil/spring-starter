package com.ablil.springstarter.authentication

import com.ablil.springstarter.domain.users.AccountStatus
import com.ablil.springstarter.domain.users.User
import com.ablil.springstarter.domain.users.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserRepositoryTest(
    @Autowired val userRepository: UserRepository,
) {

    @BeforeEach
    fun setup(): Unit = userRepository.deleteAll()

    @Test
    fun `find user by username or email`() {
        userRepository.save(user)
        assertAll(
            "fetch user from database",
            { assertNotNull(userRepository.findByUsernameOrEmail("joedoe", null)) },
            { assertNotNull(userRepository.findByUsernameOrEmail(null, "joedoe@example.com")) },
        )
    }

    @Test
    fun `update status and token by email`() {
        userRepository.save(user)
        userRepository.updateTokenAndStatus("mytoken", AccountStatus.ACTIVE, user.email)

        val actual = userRepository.findByEmail(user.email)
        assertAll(
            { assertEquals("mytoken", actual?.token) },
            { assertEquals(AccountStatus.ACTIVE, actual?.status) },
        )
    }

    @Test
    fun `update password by email`() {
        userRepository.save(user)
        userRepository.resetPassword("newsupersecurepassword", user.email)

        val actual = userRepository.findByEmail(user.email)
        assertEquals("newsupersecurepassword", actual?.password)
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
