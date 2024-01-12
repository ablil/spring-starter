package com.ablil.springstarter.persistence.common

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.EnabledIf

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIf(value = "#{environment.getActiveProfiles()[0] == 'integration'}", loadContext = true)
// TODO: check how verify integration profile is included wih contains, it does not need to be first
abstract class BaseIntegrationTest {
    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    fun createRequestWithJsonBody(body: String): HttpEntity<String> {
        return HttpEntity<String>(
            body,
            HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON },
        )
    }
}
