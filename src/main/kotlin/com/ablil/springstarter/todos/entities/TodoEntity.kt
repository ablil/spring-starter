package com.ablil.springstarter.todos.entities

import com.ablil.springstarter.common.persistence.AuditingEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "todos")
data class TodoEntity(
    @Column(nullable = false)
    val content: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val status: TodoStatus = TodoStatus.PENDING,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) : AuditingEntity()

enum class TodoStatus {
    PENDING,
    DONE,
}
