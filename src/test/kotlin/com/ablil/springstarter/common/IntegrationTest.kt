package com.ablil.springstarter.common

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [BaseIntegrationTest.Initializer::class])
annotation class IntegrationTest
