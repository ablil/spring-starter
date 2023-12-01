package com.ablil.springstarter.webapi

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Health indicators")
class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Check app health")
    fun health() = "OK"
}
