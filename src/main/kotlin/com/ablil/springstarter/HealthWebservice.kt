package com.ablil.springstarter

import com.ablil.springstarter.security.SecurityUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Health indicators")
class HealthWebservice {

    // TODO: create test for all of these endpoints

    @GetMapping("/health")
    fun health() = "health"

    @GetMapping("/private")
    fun private() = "private"

    @GetMapping("/admin")
    fun admin() = "admin"

    @GetMapping("/health/user")
    fun getCurrentUser(): String = SecurityUtil.getCurrentUser().username
}