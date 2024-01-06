package com.ablil.springstarter.service

import com.ablil.springstarter.common.UserAlreadyExists
import com.ablil.springstarter.persistence.entities.UserEntity
import com.ablil.springstarter.persistence.repositories.UserRepository
import com.ablil.springstarter.webapi.model.RegistrationRequest
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class RegistrationServiceTest {
    @Mock
    lateinit var userRepository: UserRepository

    @InjectMocks
    lateinit var registrationService: RegistrationService

    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun `should throw exception when registering an existing user`() {
        whenever(userRepository.findByUsernameOrEmail(any())).thenReturn(userEntity)

        assertThrows(UserAlreadyExists::class.java) { registrationService.register(request) }
    }

    companion object {
        val request = RegistrationRequest(
            username = "joedoe",
            email = "joedoe@example.com",
            password = "supersecurepassword",
        )
        val userEntity = UserEntity(
            id = 1343,
            username = "joedoe",
            email = "joedoe@example.com",
            password = "supersecurepassword",
        )
    }
}
