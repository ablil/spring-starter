package com.ablil.springstarter.common

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest
@ContextConfiguration(initializers = [BaseIntegrationTest.Initializer::class])
@AutoConfigureMockMvc
annotation class IntegrationTest
