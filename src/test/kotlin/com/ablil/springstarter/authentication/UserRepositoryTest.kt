package com.ablil.springstarter.authentication

import com.ablil.springstarter.domain.users.User
import com.ablil.springstarter.domain.users.UserRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserRepositoryTest(
    @Autowired val userRepository: UserRepository
) {

    @Test
    fun `find user by username or email`() {
        userRepository.save(User(
            id = null,
            username = "joedoe",
            email = "joedoe@example.com",
            password = "supersecurepassword"
        ))

        assertAll(
            "fetch user from database",
            { assertNotNull(userRepository.findByUsernameOrEmail("joedoe", null))},
            { assertNotNull(userRepository.findByUsernameOrEmail(null, "joedoe@example.com"))}
        )
    }
}