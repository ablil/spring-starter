package com.ablil.springstarter.common.testdata

import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.entities.TodoStatus

object TodoEntityFactory {
    fun random(): TodoEntity = TodoEntity(
        title = ('A'..'z').map { it }.shuffled().subList(0, 6).joinToString(""),
        status = TodoStatus.PENDING,
    ).also { it.createdBy = "johndoe" }

    fun withContent(content: String) = TodoEntity(
        title = content,
        status = TodoStatus.PENDING,
    ).also { it.createdBy = "johndoe" }
}
