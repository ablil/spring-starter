package com.ablil.springstarter.todos.services

import com.ablil.springstarter.ResourceNotFound
import com.ablil.springstarter.todos.dtos.TodoDto
import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.repositories.TodoRepository
import jakarta.validation.constraints.NotBlank
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class TodoService(val repository: TodoRepository, var authenticatedUser: UserDetails) {
    fun createTodo(
        @NotBlank content: String,
    ): TodoEntity {
        return repository.save(TodoEntity(content = content))
    }

    fun updateTodo(dto: TodoDto): TodoEntity {
        val todo = fetchTodo(requireNotNull(dto.id))
        return repository.save(todo.copy(content = dto.content, status = dto.status))
    }

    private fun fetchTodo(id: Long): TodoEntity {
        val todo = repository.findById(id).orElseThrow { ResourceNotFound("todo", id.toString()) }
            .takeIf { it.createdBy == authenticatedUser.username }
        return todo ?: throw ResourceNotFound("todo", id.toString())
    }

    fun findById(id: Long): TodoEntity {
        return fetchTodo(id)
    }

    fun deleteTodoById(id: Long) {
        repository.delete(fetchTodo(id))
    }

    fun findAll(page: Int, size: Int): Page<TodoEntity> {
        return repository.findAllByCreatedBy(
            authenticatedUser.username,
            PageRequest.of(
                page - 1,
                size,
            ),
        )
    }
}
