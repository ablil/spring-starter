package com.ablil.springstarter.todos.controllers

import com.ablil.springstarter.todos.dtos.FiltersDto
import com.ablil.springstarter.todos.dtos.TodoDto
import com.ablil.springstarter.todos.entities.TodoStatus
import com.ablil.springstarter.todos.services.TodoService
import com.ablil.springstarter.webapi.api.TodoApi
import com.ablil.springstarter.webapi.model.GetAllTodos200Response
import com.ablil.springstarter.webapi.model.Status
import com.ablil.springstarter.webapi.model.Todo
import org.springframework.data.domain.Sort.Direction
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.OffsetDateTime

@RestController
class TodoController(val service: TodoService) : TodoApi {
    override fun createTodo(todo: Todo): ResponseEntity<Todo> {
        val createTodo = service.createTodo(
            TodoDto(
                title = todo.title,
                content = todo.content,
                tags = todo.tags,
                status = todo.status?.value,
            ),
        )
        return ResponseEntity.created(URI("/api/todos/${createTodo.id}")).body(
            Todo(
                id = createTodo.id.toString(),
                title = createTodo.title,
                content = createTodo.content,
                createdAt = OffsetDateTime.parse(createTodo.createdAt.toString()),
                updatedAt = OffsetDateTime.parse(createTodo.updatedAt.toString()),
                status = Status.valueOf(createTodo.status.name),
                tags = createTodo.tags,
            ),
        )
    }

    override fun deleteTodo(id: Long): ResponseEntity<Unit> {
        service.deleteTodoById(id)
        return ResponseEntity.noContent().build()
    }

    override fun getAllTodos(
        page: Int,
        size: Int,
        keyword: String?,
        sort: String,
        order: String,
        status: Status?,
    ): ResponseEntity<GetAllTodos200Response> {
        val result = service.findAll(
            FiltersDto(
                page = page,
                size = size,
                keyword = keyword,
                status = if (status == null) null else TodoStatus.valueOf(status.name),
                order = Direction.valueOf(order),
            ),
        )
        return ResponseEntity.ok(
            GetAllTodos200Response(
                todos = result.get().map {
                    Todo(
                        id = it.id.toString(),
                        title = it.title,
                        content = it.content,
                        createdAt = OffsetDateTime.parse(it.createdAt.toString()),
                        updatedAt = OffsetDateTime.parse(it.updatedAt.toString()),
                        status = Status.valueOf(it.status.name),
                        tags = it.tags,
                    )
                }.toList(),
                total = result.totalElements.toInt(),
                page = result.number + 1,
            ),
        )
    }

    override fun getTodo(id: Long): ResponseEntity<Todo> {
        val todo = service.findById(id)
        return ResponseEntity.ok(
            Todo(
                id = todo.id.toString(),
                title = todo.title,
                content = todo.content,
                createdAt = OffsetDateTime.parse(todo.createdAt.toString()),
                updatedAt = OffsetDateTime.parse(todo.updatedAt.toString()),
                status = Status.valueOf(todo.status.name),
                tags = todo.tags,
            ),
        )
    }

    override fun updateTodo(id: Long, todo: Todo): ResponseEntity<Todo> {
        val updatedTodo = service.updateTodo(
            TodoDto(
                id = id,
                title = todo.title,
                status = todo.status?.value,
                content = todo.content,
                tags = todo.tags,
            ),
        )
        return ResponseEntity.ok(
            Todo(
                id = updatedTodo.id.toString(),
                title = updatedTodo.title,
                content = updatedTodo.content,
                status = Status.valueOf(updatedTodo.status.name),
                tags = updatedTodo.tags,
                createdAt = OffsetDateTime.parse(updatedTodo.createdAt.toString()),
                updatedAt = OffsetDateTime.parse(updatedTodo.updatedAt.toString()),
            ),
        )
    }
}
