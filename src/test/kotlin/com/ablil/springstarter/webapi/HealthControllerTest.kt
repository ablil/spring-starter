package com.ablil.springstarter.webapi

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(HealthController::class)
@AutoConfigureMockMvc(addFilters = false)
class HealthControllerTest(
    @Autowired val mockMvc: MockMvc,
) {
    @Test
    fun `should be healthy`() {
        mockMvc.get("/health").andExpectAll {
            status { isOk() }
            content { string("OK") }
        }
    }
}
