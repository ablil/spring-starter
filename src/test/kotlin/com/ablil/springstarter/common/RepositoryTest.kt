package com.ablil.springstarter.common

import com.ablil.springstarter.common.persistence.JPAConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@Import(JPAConfiguration::class)
@DataJpaTest
annotation class RepositoryTest
