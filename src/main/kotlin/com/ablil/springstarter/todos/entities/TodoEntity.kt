package com.ablil.springstarter.todos.entities

import com.ablil.springstarter.common.persistence.AuditingEntity
import com.ablil.springstarter.common.persistence.ListToStringConverter
import jakarta.persistence.*

@Entity
@Table(name = "todos")
data class TodoEntity(
    @Column(name = Fields.TITLE, nullable = false)
    val title: String,
    @Column(name = Fields.CONTENT)
    val content: String? = null,
    @Convert(converter = ListToStringConverter::class)
    @Column(name = Fields.TAGS)
    val tags: List<String>? = null,
    @Column(name = Fields.STATUS, nullable = false)
    @Enumerated(EnumType.STRING)
    val status: TodoStatus = TodoStatus.PENDING,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Fields.ID)
    val id: Long? = null,
) : AuditingEntity()

enum class TodoStatus {
    PENDING,
    DONE,
}
