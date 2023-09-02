package com.ablil.springstarter.domain.users

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Long?,

        @Column(updatable = false, nullable = false, unique = true)
        val username: String, // TODO: add email field (used when sending registration confirmation)

        @Column(nullable = false)
        val password: String,

        @Enumerated(EnumType.STRING)
        val status: AccountStatus = AccountStatus.INACTIVE,
)

enum class AccountStatus {
    INACTIVE, ACTIVE
}