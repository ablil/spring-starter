package com.ablil.springstarter.webapi

import com.ablil.springstarter.webapi.api.HealthApi
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController : HealthApi {
    override fun healthGet(): ResponseEntity<String> {
        return ResponseEntity.ok("OK")
    }
}
