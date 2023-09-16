package com.ablil.springstarter

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
) {

    @Test
    fun `should be healthy`() {
        mockMvc.get("/health").andExpect { status { isOk() } }
    }
}
