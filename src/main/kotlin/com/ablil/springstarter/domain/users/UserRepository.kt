package com.ablil.springstarter.domain.users

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: CrudRepository<User, Long> {
    fun findByUsername(username: String): User?

    fun findByUsernameOrEmail(username: String?, email: String?): User?

    fun findByToken(token: String): User?
}