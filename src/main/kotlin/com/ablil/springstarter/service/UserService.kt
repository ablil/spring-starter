package com.ablil.springstarter.service

import com.ablil.springstarter.persistence.entities.UserEntity
import com.ablil.springstarter.persistence.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun findById(userId: Long): UserEntity? = userRepository.findById(userId).orElse(null)
}
