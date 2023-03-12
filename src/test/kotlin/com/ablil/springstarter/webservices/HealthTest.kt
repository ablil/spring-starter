package com.ablil.springstarter.webservices

import com.ablil.springstarter.utils.JwtUtil
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class HealthTest(
    @Autowired val mockMvc: MockMvc
) {

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
        val token = JwtUtil.generate("testuser")
        mockMvc.get("/private") {
            headers {
                setBearerAuth(token)
            }
        }
            .andExpect {
                status { isOk() }
            }
    }
}