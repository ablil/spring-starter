package com.ablil.springstarter

import com.ablil.springstarter.domain.accounts.AccountRepository
import com.ablil.springstarter.testdata.TestAccounts
import com.ablil.springstarter.utils.JwtUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class HealthWebserviceTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val accountRepository: AccountRepository
) {

    @BeforeEach
    fun setup() {
        accountRepository.deleteAll()
    }

    @Test
    fun `should be healthy`() {
        mockMvc.get("/health").andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `should respond with forbidden`() {
        mockMvc.get("/private").andExpect {
            status { is4xxClientError() }
        }
    }

    @Test
    fun `should access private endpoint given auth token`() {
        val account = accountRepository.save(TestAccounts.activeUser)
        val token = JwtUtil.generate(account.username)

        mockMvc.get("/private") {
            headers {
                setBearerAuth(token)
            }
        }
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `should NOT access private endpoint with invalid token`() {
        val token = JwtUtil.generate("userdoesnotexists")

        mockMvc.get("/private") {
            headers {
                setBearerAuth(token)
            }
        }
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    fun `should return authenticate user username`() {
        val account = accountRepository.save(TestAccounts.activeUser)
        val token = JwtUtil.generate(account.username)

        mockMvc.get("/health/user") { headers { setBearerAuth(token) } }
            .andExpect {
                status { isOk() }
            }
    }

    @Test
    fun `should not allows access to inactive user`() {
        val account = accountRepository.save(TestAccounts.inactiveUser)
        val token = JwtUtil.generate(account.username)
        mockMvc.get("/private") { headers { setBearerAuth(token) } }.andExpect { status { isForbidden() } }
    }
}