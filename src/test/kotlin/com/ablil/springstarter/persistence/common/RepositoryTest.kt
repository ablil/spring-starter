package com.ablil.springstarter.persistence.common

import com.ablil.springstarter.persistence.JPAConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource

@Import(JPAConfiguration::class)
@DataJpaTest
@TestPropertySource(properties = ["ablil.admin-password=admin"])
annotation class RepositoryTest
