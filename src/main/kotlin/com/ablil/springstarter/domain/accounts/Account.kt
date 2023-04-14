package com.ablil.springstarter.domain.accounts

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Account(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) val id: Long?,
    val username: String,
    val password: String,
)
