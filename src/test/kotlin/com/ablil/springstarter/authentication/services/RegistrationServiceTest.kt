package com.ablil.springstarter.authentication.services

import com.ablil.springstarter.ResetPasswordError
import com.ablil.springstarter.UserAlreadyExists
import com.ablil.springstarter.authentication.controllers.RegistrationDto
import com.ablil.springstarter.common.testdata.UserEntityFactory
import com.ablil.springstarter.mail.MailService
import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.repositories.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class RegistrationServiceTest {
    val repository: UserRepository = mock {
        on { save(any<UserEntity>()) } doAnswer { it.getArgument(0) }
    }
    val passwordEncoder: PasswordEncoder = mock {
        on { encode(any<CharSequence>()) } doAnswer { "{noop}${it.getArgument<CharSequence>(0)}" }
    }
    val mailService: MailService = mock()
    val service = RegistrationService(repository, passwordEncoder, mailService)

    @Nested
    inner class NewUserRegistrationTests {
        @Test
        fun `register new user and send confirmation email`() {
            service.registerNewUser(
                RegistrationDto(
                    "johndoe",
                    "johndoe@example.com",
                    "supersecurepassword",
                ),
            )

            verify(repository).save(
                org.mockito.kotlin.check {
                    assertAll(
                        { assertEquals("johndoe", it.username) },
                        { assertEquals("johndoe@example.com", it.email) },
                        { assertNotEquals("supersecurepassword", it.password) },
                        { assertEquals(AccountStatus.INACTIVE, it.status) },
                        { assertNotNull(it.token) },
                    )
                },
            )

            verify(mailService).confirmRegistration(
                org.mockito.kotlin.check { assertEquals("johndoe@example.com", it) },
                org.mockito.kotlin.check { assertNotNull(it) },
            )
        }

        @Test
        fun `throw exception given an existing user by username`() {
            whenever(repository.findByUsername("johndoe"))
                .thenReturn(UserEntityFactory.newUser())

            assertThrows<UserAlreadyExists> {
                service.registerNewUser(
                    RegistrationDto(
                        "johndoe",
                        "johndoe@example.com",
                        "supersecurepassword",
                    ),
                )
            }
        }

        @Test
        fun `throw exception given an existing user by email`() {
            whenever(repository.findByEmail("johndoe@example.com"))
                .thenReturn(UserEntityFactory.newUser())

            assertThrows<UserAlreadyExists> {
                service.registerNewUser(
                    RegistrationDto(
                        "johndoe",
                        "johndoe@example.com",
                        "supersecurepassword",
                    ),
                )
            }
        }
    }

    @Nested
    inner class PasswordResetTests {
        @Test
        fun `send password link successfully`() {
            whenever(repository.findByEmail("johndoe@example.com"))
                .thenReturn(UserEntityFactory.newUser().copy(email = "johndoe@example.com"))

            service.sendPasswordResetLink("johndoe@example.com")

            verify(repository).save(
                org.mockito.kotlin.check {
                    assertEquals(AccountStatus.PASSWORD_RESET_IN_PROGRESS, it.status)
                    assertThat(it.token).isNotBlank()
                },
            )
            verify(mailService).sendResetPasswordLink(
                org.mockito.kotlin.check { assertEquals("johndoe@example.com", it) },
                org.mockito.kotlin.check { assertThat(it).isNotBlank() },
            )
        }

        @Test
        fun `reset user password successfully`() {
            whenever(repository.findByToken("token"))
                .thenReturn(UserEntityFactory.newUser().copy(email = "johndoe@example.com"))

            service.resetUserPassword("token", "supersecurepassword")

            verify(repository).save(
                org.mockito.kotlin.check {
                    assertNull(it.token)
                    assertEquals(AccountStatus.ACTIVE, it.status)
                    assertNotEquals("supersecurepassword", it.password)
                    assertEquals("johndoe@example.com", it.email)
                },
            )
            verify(mailService).notifyUserWithPasswordChange("johndoe@example.com")
        }

        @Test
        fun `throw exception when trying to reset password for non existing user`() {
            whenever(repository.findByToken("token")).thenReturn(null)

            assertThrows<ResetPasswordError> {
                service.resetUserPassword("token", "supersecurepassword")
            }
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
