package com.ablil.springstarter.todos.dtos

import com.ablil.springstarter.todos.entities.Fields
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
    ID(Fields.ID),
    TITLE(Fields.TITLE),
    CREATED_AT(Fields.CREATED_AT),
    UPDATED_AT(Fields.UPDATED_AT),
}

data class FiltersDto(
    val offset: Int = 0,
    val limit: Int = 20,
    val keyword: String? = null,
    val status: TodoStatus? = null,
    val sortBy: SortBy? = null,
    val order: Direction = Direction.ASC,
)
