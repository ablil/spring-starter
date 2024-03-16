package com.ablil.springstarter.users.controllers

import com.ablil.springstarter.users.services.UserService
import com.ablil.springstarter.webapi.api.UserApi
import com.ablil.springstarter.webapi.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class UserController(private val userService: UserService) : UserApi {
    override fun getUser(userId: BigDecimal): ResponseEntity<User> {
        return userService.findById(userId.toLong())?.let {
            User(
                id = it.id.toString(),
                username = it.username,
                email = it.email,
                status = it.status.name,
                role = User.Role.valueOf(it.role.name),
            )
        }?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }
}
