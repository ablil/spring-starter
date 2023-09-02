package com.ablil.springstarter.authentication

import com.ablil.springstarter.domain.users.AccountStatus
import com.ablil.springstarter.domain.users.UserRepository
import com.ablil.springstarter.miscllaneous.EmailClient
import com.ninjasquad.springmockk.MockkBean
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RegistrationServiceTest(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val registrationService: RegistrationService,
    @MockkBean(relaxed = true) @Autowired private val emailClient: EmailClient
) {

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
    }

    @Test
    fun `should register new account successfully`() {
        val user = registrationService.register(request)
        assertAll("user registration",
            { assertNotNull(user) },
            { assertEquals(AccountStatus.INACTIVE, user.status) },
            { assertNotEquals(request.password, user.password) },
            { assertNotNull(user.token) }
        )
        verify { emailClient.sendEmail(user.email, "Confirm registration", requireNotNull(user.token)) }
    }

    @Test
    fun `should throw exception when registering an existing user`() {
        registrationService.register(request)
        assertThrows(UserAlreadyExists::class.java) {
            registrationService.register(request)
        }
    }

    @Test
    fun `should confirm registration`() {
        registrationService.register(request).also {
            registrationService.confirmRegistration(requireNotNull(it.token))
        }

        val user = userRepository.findByUsername("joedoe")
        assertAll(
            { assertNull(user?.token) },
            { assertEquals(AccountStatus.ACTIVE, user?.status) }
        )
    }

    companion object {
        val request = RegistrationRequest(
            username = "joedoe",
            email = "joedoe@example.com",
            password = "supersecurepassword"
        )
    }
}