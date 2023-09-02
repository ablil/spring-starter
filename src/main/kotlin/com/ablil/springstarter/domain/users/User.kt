package com.ablil.springstarter.domain.users

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
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
)

enum class AccountStatus {
    INACTIVE, ACTIVE
}