package com.ablil.springstarter.authentication.services

import com.ablil.springstarter.InvalidCredentials
import com.ablil.springstarter.authentication.controllers.LoginCredentials
import com.ablil.springstarter.mail.MailService
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.internal.verification.Times
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class LoginServiceTest {
    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    @Mock
    lateinit var mailService: MailService

    @InjectMocks
    lateinit var loginService: LoginService

    @Test
    fun `should generate jwt token given valid credentials`() {
        whenever(passwordEncoder.matches(any(), any())).thenReturn(true)
        whenever(userRepository.findByUsernameOrEmail(any())).thenReturn(
            userEntity.copy(
                status = AccountStatus.ACTIVE,
            ),
        )

        assertNotNull(loginService.login(LoginCredentials("joedoe", "supersecurepassword")))
    }

    @Test
    fun `should throw exception given invalid credentials`() {
        whenever(passwordEncoder.matches(any(), any())).thenReturn(false)
        whenever(userRepository.findByUsernameOrEmail(any())).thenReturn(userEntity)

        assertThrows(InvalidCredentials::class.java) {
            loginService.login(LoginCredentials("joedoe", "supersecurepassword"))
        }
    }

    @Test
    fun `reset password successfully`() {
        whenever(userRepository.findByToken("token")).thenReturn(userEntity)
        whenever(passwordEncoder.encode(any())).thenReturn("supersecurepassword")

        loginService.resetPassword("token", "supersecurepassword")

        verify(userRepository, Times(1))
            .updateTokenAndStatus(null, AccountStatus.ACTIVE, userEntity.email)
        verify(userRepository, Times(1))
            .resetPassword("supersecurepassword", userEntity.email)
    }

    @Test
    fun `deny login for inactive users`() {
        whenever(passwordEncoder.matches(any(), any())).thenReturn(true)
        whenever(userRepository.findByUsernameOrEmail(any()))
            .thenReturn(userEntity.copy(status = AccountStatus.INACTIVE))

        assertThrows(InvalidCredentials::class.java) {
            loginService.login(LoginCredentials("joedoe", "supersecurepassword"))
        }
    }

    companion object {
        val userEntity = UserEntity(
            id = 1343,
            username = "joedoe",
            email = "joedoe@example.com",
            password = "supersecurepassword",
        )
    }
}
