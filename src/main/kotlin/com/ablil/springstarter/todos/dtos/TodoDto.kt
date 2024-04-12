package com.ablil.springstarter.todos.dtos

import com.ablil.springstarter.todos.entities.TodoStatus

data class TodoDto(val content: String, val status: TodoStatus, val id: Long?)
