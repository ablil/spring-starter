package com.ablil.springstarter.common

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.userdetails.User
import java.util.Optional

@TestConfiguration
class WithMockUserContext {
    @Bean
    fun integrationAuditorProvider(): AuditorAware<String> = AuditorAware<String> { Optional.of("johndoe") }

    @Bean
    fun authenticatedUser() = User("johndoe", "{noop}supersecurepassword", emptyList())
}
