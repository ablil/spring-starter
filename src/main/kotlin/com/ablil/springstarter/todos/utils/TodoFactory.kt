package com.ablil.springstarter.todos.utils

import com.ablil.springstarter.todos.entities.TagEntity
import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.entities.TodoStatus

object TodoFactory {
    fun create(title: String, content: String, tags: List<TagEntity>): TodoEntity =
        create(title, content, TodoStatus.PENDING, tags)

    fun create(title: String, content: String?, status: TodoStatus, tags: List<TagEntity>?): TodoEntity {
        return TodoEntity(
            title = title,
            content = content,
            tags = tags,
            status = status,
        )
    }

    fun create(title: String, content: String, status: TodoStatus): TodoEntity =
        create(title, content, status, emptyList())

    fun create(title: String, content: String): TodoEntity = create(title, content, TodoStatus.PENDING, emptyList())

    fun create(title: String): TodoEntity = create(title, null, TodoStatus.PENDING, null)
}
