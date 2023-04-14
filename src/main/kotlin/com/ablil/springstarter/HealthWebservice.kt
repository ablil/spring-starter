package com.ablil.springstarter

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthWebservice {

    @GetMapping("/health")
    fun health() = "health"

    @GetMapping("/private")
    fun private() = "private"

    @GetMapping("/admin")
    fun admin() = "admin"

    @GetMapping("/health/user")
    fun getCurrentUser() = (SecurityContextHolder.getContext().authentication.principal as UserDetails).username
}