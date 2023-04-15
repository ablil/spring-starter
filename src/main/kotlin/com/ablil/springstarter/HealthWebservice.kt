package com.ablil.springstarter

import com.ablil.springstarter.utils.SecurityUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Health", description = "Check application health")
class HealthWebservice {

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