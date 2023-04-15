package com.ablil.springstarter.domain.accounts

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Account(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long?,
    @Column(
        updatable = false,
        nullable = false
    ) val username: String, // this could be used as an email without renaming the field
    @Column(nullable = false) val password: String,

    val firstName: String?,
    val lastName: String?,

    val status: AccountStatus,
)

enum class AccountStatus {
    INACTIVE, ACTIVE
}