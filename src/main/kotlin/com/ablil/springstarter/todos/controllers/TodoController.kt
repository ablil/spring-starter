package com.ablil.springstarter.todos.controllers

import com.ablil.springstarter.todos.converters.TodoConverter
import com.ablil.springstarter.todos.dtos.FiltersDto
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

@RestController
class TodoController(val service: TodoService) : TodoApi {
    override fun createTodo(todo: Todo): ResponseEntity<Todo> {
        val createTodo = service.createTodo(TodoConverter.INSTANCE.modelToDto(todo))
        return ResponseEntity
            .created(URI("/api/todos/${createTodo.id}"))
            .body(TodoConverter.INSTANCE.entityToModel(createTodo))
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
                todos = result.get().map { TodoConverter.INSTANCE.entityToModel(it) }.toList(),
                total = result.totalElements.toInt(),
                page = result.number + 1,
            ),
        )
    }

    override fun getTodo(id: Long): ResponseEntity<Todo> {
        return ResponseEntity.ok(TodoConverter.INSTANCE.entityToModel(service.findById(id)))
    }

    override fun updateTodo(id: Long, todo: Todo): ResponseEntity<Todo> {
        val updatedTodo = service.updateTodo(TodoConverter.INSTANCE.modelToDto(todo).copy(id = id))
        return ResponseEntity.ok(TodoConverter.INSTANCE.entityToModel(updatedTodo))
    }
}
