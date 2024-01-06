package com.ablil.springstarter.service

import com.ablil.springstarter.common.InvalidCredentials
import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.entities.UserEntity
import com.ablil.springstarter.persistence.repositories.UserRepository
import com.ablil.springstarter.webapi.LoginCredentials
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class LoginServiceTest {
    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    lateinit var loginService: LoginService

    @Test
    fun `should generate jwt token given valid credentials`() {
        Mockito.`when`(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true)
        Mockito.`when`(
            userRepository.findByUsernameOrEmail(Mockito.any(), Mockito.any()),
        ).thenReturn(userEntity.copy(status = AccountStatus.ACTIVE))
        val token = loginService.login(LoginCredentials("joedoe", "supersecurepassword"))
        assertNotNull(token)
    }

    @Test
    fun `should throw exception given invalid credentials`() {
        Mockito.`when`(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(false)
        Mockito.`when`(
            userRepository.findByUsernameOrEmail(Mockito.any(), Mockito.any()),
        ).thenReturn(userEntity)
        assertThrows(InvalidCredentials::class.java) {
            loginService.login(LoginCredentials("joedoe", "supersecurepassword"))
        }
    }

    @Test
    fun `reset password successfully`() {
        Mockito.`when`(userRepository.findByToken("token")).thenReturn(userEntity)
        Mockito.`when`(passwordEncoder.encode(Mockito.any())).thenReturn("supersecurepassword")

        loginService.resetPassword("token", "supersecurepassword")

        Mockito.verify(
            userRepository,
            Mockito.times(1),
        ).updateTokenAndStatus(null, AccountStatus.ACTIVE, userEntity.email)
        Mockito.verify(
            userRepository,
            Mockito.times(1),
        ).resetPassword("supersecurepassword", userEntity.email)
    }

    @Test
    fun `deny login for inactive users`() {
        Mockito.`when`(passwordEncoder.matches(Mockito.any(), Mockito.any())).thenReturn(true)
        Mockito.`when`(
            userRepository.findByUsernameOrEmail(Mockito.any(), Mockito.any()),
        ).thenReturn(userEntity.copy(status = AccountStatus.INACTIVE))

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
