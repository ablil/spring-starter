package com.ablil.springstarter.service.common

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ComponentScan("com.ablil.springstarter.service")
annotation class ServiceTest()
