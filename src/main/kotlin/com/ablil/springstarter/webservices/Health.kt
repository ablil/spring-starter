package com.ablil.springstarter.webservices

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Health {

    @GetMapping("/health")
    fun health() = "health"

    @GetMapping("/private")
    fun private() = "private"

    @GetMapping("/admin")
    fun admin() = "admin"
}