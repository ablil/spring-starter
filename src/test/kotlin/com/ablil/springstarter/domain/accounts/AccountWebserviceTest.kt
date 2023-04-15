package com.ablil.springstarter.domain.accounts

import com.ablil.springstarter.testdata.AccountsTestData
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class AccountWebserviceTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val accountRepository: AccountRepository,
    @Autowired private val accountService: AccountService,
) {

    private val testAccount = AccountsTestData.inactiveUser
    private val registrationRequest = AccountsTestData.fullRegistrationRequest

    @BeforeEach
    fun setup() {
        accountRepository.deleteAll()
    }

    @Test
    fun `should register user given username and password only`() {
        val request = """
                {
                    "username": "${registrationRequest.username}",
                    "password": "${registrationRequest.password}"
                }
            """.trimIndent()

        mockMvc.post("/register") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect { status { isCreated() } }

        val account = accountRepository.findByUsername(registrationRequest.username)
        assertNotNull(account)
        assertEquals(registrationRequest.username, account?.username)
        assertNotEquals(registrationRequest.password, account?.password)
        assertEquals(AccountStatus.INACTIVE, account?.status)
    }

    @Test
    fun `should register user given full registration request`() {
        val request: String = """
            {
                "username": "${registrationRequest.username}",
                "password": "${registrationRequest.password}",
                "firstName": "${registrationRequest.firstName}",
                "lastName": "${registrationRequest.lastName}"
            }
        """.trimIndent()

        mockMvc.post("/register") {
            contentType = MediaType.APPLICATION_JSON
            content = request
        }.andExpect { status { isCreated() } }

        val registered = accountRepository.findByUsername(testAccount.username)
        assertNotNull(registered)
        assertEquals(registrationRequest.username, registered?.username)
        assertNotEquals(registrationRequest.password, registered?.password)
        assertEquals(AccountStatus.INACTIVE, registered?.status)
        assertEquals(registrationRequest.firstName, registered?.firstName)
        assertEquals(registrationRequest.lastName, registered?.lastName)
    }

    @Test
    fun `should login user successfully`() {
        accountService.register(testAccount.username, testAccount.password)
        accountService.activate(testAccount.username)

        mockMvc.post("/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"username": "${testAccount.username}", "password": "${testAccount.password}"}
            """.trimIndent()
        }.andExpectAll {
            status { isOk() }
            jsonPath("$.token") { exists() }
        }
    }

    @Test
    fun `should not allow login for inactive user`() {
        accountService.register(testAccount.username, testAccount.password)

        mockMvc.post("/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"username": "${AccountsTestData.inactiveUser.username}", "password": "${AccountsTestData.inactiveUser.password}" }
            """.trimIndent()
        }.andExpect { status { isUnauthorized() } }
    }

}