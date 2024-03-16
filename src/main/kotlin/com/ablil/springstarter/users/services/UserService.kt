package com.ablil.springstarter.users.services

import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun findById(userId: Long): UserEntity? = userRepository.findById(userId).orElse(null)
}
