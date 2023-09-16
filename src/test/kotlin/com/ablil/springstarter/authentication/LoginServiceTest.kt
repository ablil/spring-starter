package com.ablil.springstarter.authentication

import com.ablil.springstarter.domain.users.AccountStatus
import com.ablil.springstarter.domain.users.User
import com.ablil.springstarter.domain.users.UserRepository
import com.ablil.springstarter.miscllaneous.EmailClient
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootTest
class LoginServiceTest(
    @Autowired val loginService: LoginService,
    @MockkBean(relaxed = true) @Autowired val userRepository: UserRepository,
    @MockkBean(relaxed = true) @Autowired val emailClient: EmailClient,
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

    @Test
    fun `set reset password link and change user status`() {
        every { userRepository.findByEmail(any()) } returns user
        loginService.forgetPassword(user.email)
        verify {
            userRepository.updateTokenAndStatus(
                withArg { assertNotNull(it) },
                withArg { assertEquals(AccountStatus.PASSWORD_RESET_IN_PROGRESS, it) },
                withArg { assertEquals(user.email, it) }
            )
            emailClient.sendEmail(user.email, "Reset password", withArg { assertNotNull(it) })
        }
    }

    @Test
    fun `reset password successfully`() {
        every { userRepository.findByToken(any()) } returns user
        loginService.resetPassword(ResetPassword("token", "supersecurepassword"))
        verify {
            userRepository.updateTokenAndStatus(null, AccountStatus.ACTIVE, user.email)
            userRepository.resetPassword(withArg { assertNotEquals(user.password, it) }, user.email)
        }
    }

    companion object {
        val user = User(
            id = 1343,
            username = "joedoe",
            email = "joedoe@example.com",
            password = "supersecurepassword"
        )
    }
}
