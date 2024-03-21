package com.ablil.springstarter.authentication.services

import com.ablil.springstarter.InvalidCredentials
import com.ablil.springstarter.authentication.controllers.Credentials
import com.ablil.springstarter.common.JwtUtil
import com.ablil.springstarter.common.testdata.UserEntityFactory
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class AuthenticationServiceTest {
    val userRepository: UserRepository = mock()
    val passwordEncoder: PasswordEncoder = mock()

    val authenticationService = AuthenticationService(userRepository, passwordEncoder)

    @Test
    fun `generate bearer token given valid credentials`() {
        whenever(passwordEncoder.matches(any(), any()))
            .thenReturn(true)
        whenever(userRepository.findByUsernameOrEmail("johndoe"))
            .thenReturn(UserEntityFactory.newUser().copy(status = AccountStatus.ACTIVE))

        val credentials = Credentials("johndoe", "supersecurepassword")
        val expectedToken = JwtUtil.generate("johndoe")
        val actualToken = authenticationService.validateCredentialsAndGenerateToken(credentials)
        assertEquals(expectedToken, actualToken)
    }

    @Nested
    inner class InvalidCredentialsTests {
        val credentials = Credentials("johndoe", "supersecurepassword")

        @Test
        fun `throw exception given non existing account`() {
            whenever(userRepository.findByUsernameOrEmail("joedoe"))
                .thenReturn(null)

            org.junit.jupiter.api.assertThrows<InvalidCredentials>("account not found") {
                authenticationService.validateCredentialsAndGenerateToken(credentials)
            }
        }

        @Test
        fun `throw exception given inactive account`() {
            whenever(userRepository.findByUsernameOrEmail("joedoe"))
                .thenReturn(UserEntityFactory.newUser().copy(status = AccountStatus.INACTIVE))

            org.junit.jupiter.api.assertThrows<InvalidCredentials>("account disabled") {
                authenticationService.validateCredentialsAndGenerateToken(credentials)
            }
        }

        @Test
        fun `throw exception given invalid password`() {
            whenever(userRepository.findByUsernameOrEmail("joedoe"))
                .thenReturn(UserEntityFactory.newUser().copy(status = AccountStatus.ACTIVE))
            whenever(passwordEncoder.matches(any(), any()))
                .thenReturn(false)

            org.junit.jupiter.api.assertThrows<InvalidCredentials>("invalid credentials") {
                authenticationService.validateCredentialsAndGenerateToken(credentials)
            }
        }
    }

    @Test
    fun `deny login for inactive users`() {
        whenever(passwordEncoder.matches(any(), any())).thenReturn(true)
        whenever(userRepository.findByUsernameOrEmail(any()))
            .thenReturn(userEntity.copy(status = AccountStatus.INACTIVE))

        assertThrows(InvalidCredentials::class.java) {
            authenticationService.validateCredentialsAndGenerateToken(
                Credentials("joedoe", "supersecurepassword"),
            )
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
