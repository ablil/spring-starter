package com.ablil.springstarter.persistence.repositories

import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.entities.User
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?

    fun findByUsernameOrEmail(username: String?, email: String?): User?

    fun findByToken(token: String): User?

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update User u set u.token = :token, u.status = :status where u.email = :email")
    fun updateTokenAndStatus(token: String?, status: AccountStatus, email: String)

    @Transactional
    @Modifying
    @Query("update User u set u.password = :password where u.email = :email")
    fun resetPassword(@Param("password") password: String, @Param("email") email: String): Int
}
