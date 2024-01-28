package com.ablil.springstarter.integration.common

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.testcontainers.containers.PostgreSQLContainer

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [BaseIntegrationTest.Initializer::class])
annotation class IntegrationTest

@EnabledIf(expression = "#{systemProperties['spring.profiles.active'].contains('integration')}")
abstract class BaseIntegrationTest {
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
