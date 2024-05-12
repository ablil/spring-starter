package com.ablil.springstarter.todos.entities

import com.ablil.springstarter.common.persistence.AuditingEntity
import com.ablil.springstarter.common.persistence.ListToStringConverter
import jakarta.persistence.*

@Entity
@Table(name = "todos")
data class TodoEntity(
    @Column(nullable = false)
    val title: String,
    val content: String? = null,
    @Convert(converter = ListToStringConverter::class)
    val tags: List<String>? = null,
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
