package com.ablil.springstarter.todos.dtos

import com.ablil.springstarter.todos.entities.TodoStatus
import org.springframework.data.domain.Sort.Direction

data class TodoDto(
    val title: String,
    val content: String? = null,
    val status: String? = null,
    val tags: List<String>? = null,
    val id: Long? = null,
)

enum class SortBy(val value: String) {
    ID("id"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt"),
}

data class FiltersDto(
    val page: Int = 1,
    val size: Int = 20,
    val keyword: String? = null,
    val status: TodoStatus? = null,
    val sortBy: SortBy? = null,
    val order: Direction = Direction.ASC,
)
