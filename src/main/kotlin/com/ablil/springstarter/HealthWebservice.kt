package com.ablil.springstarter

import com.ablil.springstarter.security.SecurityUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Health", description = "Check application health")
class HealthWebservice {

    // TODO: create test for all of these endpoints

    @GetMapping("/health")
    @Operation(summary = "Check app health")
    fun health() = "health"

    @GetMapping("/private")
    @Operation(summary = "Check app health with private endpoint")
    fun private() = "private"

    @GetMapping("/admin")
    @Operation(summary = "Check app health for admin users")
    fun admin() = "admin"

    @GetMapping("/health/user")
    @Operation(summary = "Get authenticated user username")
    fun getCurrentUser(): String = SecurityUtil.getCurrentUser().username
}