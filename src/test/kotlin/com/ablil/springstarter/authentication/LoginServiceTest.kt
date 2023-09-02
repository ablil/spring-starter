package com.ablil.springstarter.authentication

import com.ablil.springstarter.domain.users.User
import com.ablil.springstarter.domain.users.UserRepository
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest
class LoginServiceTest(
    @Autowired val loginService: LoginService,
    @MockkBean(relaxed = true) @Autowired val userRepository: UserRepository,
    @Autowired val passwordEncoder: PasswordEncoder
) {

    @Test
    fun `should generate jwt token given valid credentials`() {
        every { userRepository.findByUsernameOrEmail(any(), any()) } returns user.copy(password = passwordEncoder.encode("supersecurepassword"))
        val token = loginService.login(LoginCredentials("joedoe", "supersecurepassword"))
        assertNotNull(token)
    }

    @Test
    fun `should throw exception given invalid credentials`() {
        every { userRepository.findByUsernameOrEmail(any(), any()) } returns user.copy(password = passwordEncoder.encode("anotherpassword"))
        assertThrows(InvalidCredentials::class.java) {
            loginService.login(LoginCredentials("joedoe", "supersecurepassword"))
        }
    }

    companion object {
        val user = User(
            id = null,
            username = "joedoe",
            email = "joedoe@example.com",
            password = "supersecurepassword"
        )
    }
}