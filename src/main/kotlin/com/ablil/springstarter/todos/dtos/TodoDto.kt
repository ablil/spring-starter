package com.ablil.springstarter.todos.dtos

import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.entities.TodoStatus

data class TodoDto(
    val title: String,
    val content: String? = null,
    val status: String? = null,
    val tags: List<String>? = null,
    val id: Long? = null,
) {
    fun toEntity(): TodoEntity {
        return TodoEntity(
            id = this.id,
            title = this.title,
            content = this.content,
            status = status?.let { TodoStatus.valueOf(it) } ?: TodoStatus.PENDING,
            tags = this.tags,
        )
    }
}
