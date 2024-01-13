package com.ablil.springstarter.persistence.common

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIf(value = "#{environment.getActiveProfiles()[0] == 'integration'}", loadContext = true)
@ContextConfiguration(initializers = [BaseIntegrationTest.Initializer::class])
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

    companion object {
        val postgres = PostgreSQLContainer("postgres:15-alpine")
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            postgres.start()
            TestPropertyValues.of(
                "spring.datasource.url=${postgres.jdbcUrl}",
                "spring.datasource.username=${postgres.username}",
                "spring.datasource.password=${postgres.password}",
            ).applyTo(applicationContext.environment)
        }
    }
}
