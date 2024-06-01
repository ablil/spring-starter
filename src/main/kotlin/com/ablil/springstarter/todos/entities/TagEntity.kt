package com.ablil.springstarter.todos.entities

import jakarta.persistence.*

@Entity
@Table(name = "tags")
data class TagEntity(
    val tag: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    lateinit var todo: TodoEntity
}
