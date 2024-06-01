package com.ablil.springstarter.todos.entities

import com.ablil.springstarter.common.persistence.AuditingEntity
import jakarta.persistence.*

@Entity
@Table(name = "todos")
data class TodoEntity(
    @Column(name = Fields.TITLE, nullable = false)
    val title: String,
    @Column(name = Fields.CONTENT)
    val content: String? = null,
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "todo")
    val tags: List<TagEntity>? = null,
    @Column(name = Fields.STATUS, nullable = false)
    @Enumerated(EnumType.STRING)
    val status: TodoStatus = TodoStatus.PENDING,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Fields.ID)
    val id: Long? = null,
) : AuditingEntity() {
    @PrePersist
    @PreUpdate
    fun populateForeignKeyReference() {
        this.tags?.forEach { tag -> tag.todo = this }
    }
}

enum class TodoStatus {
    PENDING,
    DONE,
}
