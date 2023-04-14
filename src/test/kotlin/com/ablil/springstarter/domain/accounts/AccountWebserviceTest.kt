package com.ablil.springstarter.domain.accounts

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

    @BeforeEach
    fun setup() {
        accountRepository.deleteAll()
    }

    @Test
    fun `should register user`() {
        mockMvc.post("/register") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                { "username": "testuser", "password": "supersecretpassword" }
            """.trimIndent()
        }.andExpect { status { isCreated() } }

        val account = accountRepository.findByUsername("testuser")
        assertNotNull(account)
        assertEquals("testuser", account?.username)
        assertNotEquals("supersecretpassword", account?.password)
    }

    @Test
    fun `should login user successfully`() {
        accountService.register("testuser", "supersecretpassword")

        mockMvc.post("/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {"username": "testuser", "password": "supersecretpassword"}
            """.trimIndent()
        }.andExpectAll {
            status { isOk() }
            jsonPath("$.token") { exists() }
        }
    }
}