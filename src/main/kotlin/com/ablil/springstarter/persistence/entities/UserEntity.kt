package com.ablil.springstarter.persistence.entities

import com.ablil.springstarter.persistence.common.AuditingEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long?,

    @Column(updatable = false, nullable = false, unique = true)
    val username: String,

    @Column(updatable = false, nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Enumerated(EnumType.STRING)
    val status: AccountStatus = AccountStatus.INACTIVE,

    val token: String? = null,

    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.USER,
) : AuditingEntity()

enum class AccountStatus {
    INACTIVE, ACTIVE, PASSWORD_RESET_IN_PROGRESS
}

enum class UserRole {
    ADMIN, USER
}
