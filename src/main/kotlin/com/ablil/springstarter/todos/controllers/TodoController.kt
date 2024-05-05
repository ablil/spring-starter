package com.ablil.springstarter.todos.controllers

import com.ablil.springstarter.todos.dtos.TodoDto
import com.ablil.springstarter.todos.entities.TodoStatus
import com.ablil.springstarter.todos.services.TodoService
import com.ablil.springstarter.webapi.api.TodoApi
import com.ablil.springstarter.webapi.model.CreateTodoRequest
import com.ablil.springstarter.webapi.model.FullTodo
import com.ablil.springstarter.webapi.model.GetAllTodos200Response
import com.ablil.springstarter.webapi.model.Todo
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.OffsetDateTime

@RestController
@Tag(name = "Todo", description = "Everything about managing todos")
@SecurityRequirement(name = "bearerAuth")
class TodoController(val service: TodoService) : TodoApi {
    override fun createTodo(createTodoRequest: CreateTodoRequest): ResponseEntity<FullTodo> {
        val createTodo = service.createTodo(createTodoRequest.content)
        return ResponseEntity.created(URI("/api/todos/${createTodo.id}")).body(
            FullTodo(
                id = createTodo.id.toString(),
                content = createTodo.content,
                createdAt = OffsetDateTime.parse(createTodo.createdAt.toString()),
                updatedAt = OffsetDateTime.parse(createTodo.updatedAt.toString()),
                status = FullTodo.Status.valueOf(createTodo.status.name),
            ),
        )
    }

    override fun deleteTodo(id: Long): ResponseEntity<Unit> {
        service.deleteTodoById(id)
        return ResponseEntity.noContent().build()
    }

    override fun getAllTodos(page: Int, size: Int): ResponseEntity<GetAllTodos200Response> {
        val result = service.findAll(page, size)
        return ResponseEntity.ok(
            GetAllTodos200Response(
                todos = result.get().map {
                    FullTodo(
                        id = it.id.toString(),
                        content = it.content,
                        createdAt = OffsetDateTime.parse(it.createdAt.toString()),
                        updatedAt = OffsetDateTime.parse(it.updatedAt.toString()),
                        status = FullTodo.Status.valueOf(it.status.name),
                    )
                }.toList(),
                total = result.totalElements.toInt(),
                page = result.number + 1,
            ),
        )
    }

    override fun getTodo(id: Long): ResponseEntity<FullTodo> {
        val todo = service.findById(id)
        return ResponseEntity.ok(
            FullTodo(
                id = todo.id.toString(),
                content = todo.content,
                createdAt = OffsetDateTime.parse(todo.createdAt.toString()),
                updatedAt = OffsetDateTime.parse(todo.updatedAt.toString()),
                status = FullTodo.Status.valueOf(todo.status.name),
            ),
        )
    }

    override fun updateTodo(id: Long, todo: Todo): ResponseEntity<Todo> {
        val updatedTodo = service.updateTodo(
            TodoDto(
                id = id,
                status = TodoStatus.valueOf(todo.status.value),
                content = requireNotNull(todo.content),
            ),
        )
        return ResponseEntity.ok(
            Todo(
                content = updatedTodo.content,
                status = Todo.Status.valueOf(updatedTodo.status.name),
            ),
        )
    }
}
