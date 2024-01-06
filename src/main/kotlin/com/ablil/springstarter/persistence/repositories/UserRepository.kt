package com.ablil.springstarter.persistence.repositories

import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.entities.UserEntity
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepository : CrudRepository<UserEntity, Long> {
    fun findByUsername(username: String): UserEntity?

    fun findByEmail(email: String): UserEntity?

    @Query("select u from UserEntity u where u.username = :username or u.email = :username")
    fun findByUsernameOrEmail(username: String): UserEntity?

    fun findByToken(token: String): UserEntity?

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("update UserEntity u set u.token = :token, u.status = :status where u.email = :email")
    fun updateTokenAndStatus(token: String?, status: AccountStatus, email: String)

    @Transactional
    @Modifying
    @Query("update UserEntity u set u.password = :password where u.email = :email")
    fun resetPassword(
        @Param("password") password: String,
        @Param("email") email: String,
    ): Int
}
