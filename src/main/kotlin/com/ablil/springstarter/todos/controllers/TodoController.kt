package com.ablil.springstarter.todos.controllers

import com.ablil.springstarter.common.logger
import com.ablil.springstarter.todos.converters.TodoConverter
import com.ablil.springstarter.todos.dtos.FiltersDto
import com.ablil.springstarter.todos.dtos.SortBy
import com.ablil.springstarter.todos.entities.TodoStatus
import com.ablil.springstarter.todos.services.TodoService
import com.ablil.springstarter.webapi.api.TodoApi
import com.ablil.springstarter.webapi.model.GetAllTodos200Response
import com.ablil.springstarter.webapi.model.Pagination
import com.ablil.springstarter.webapi.model.Status
import com.ablil.springstarter.webapi.model.Todo
import org.springframework.data.domain.Sort.Direction
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class TodoController(val service: TodoService) : TodoApi {
    val logger by logger()

    override fun createTodo(todo: Todo): ResponseEntity<Todo> {
        logger.info("Got request to create todo {}", todo)
        val createTodo = service.createTodo(TodoConverter.INSTANCE.modelToDto(todo))
        return ResponseEntity
            .created(URI("/api/todos/${createTodo.id}"))
            .body(TodoConverter.INSTANCE.entityToModel(createTodo))
    }

    override fun deleteTodo(id: Long): ResponseEntity<Unit> {
        logger.info("Got request to delete todo {}", id)
        service.deleteTodoById(id)
        return ResponseEntity.noContent().build()
    }

    override fun getAllTodos(
        offset: Int?,
        limit: Int?,
        keyword: String?,
        sort: String?,
        status: Status?,
    ): ResponseEntity<GetAllTodos200Response> {
        logger.info(
            "Got request to filter todos: offset={}, limit={}, keyword={}, status={}",
            offset,
            limit,
            keyword,
            status,
        )
        val result = service.findAll(
            FiltersDto(
                offset = offset ?: 0,
                limit = limit ?: 50,
                keyword = keyword,
                status = if (status == null) null else TodoStatus.valueOf(status.name),
                sortBy = sort?.trim('-', '+')?.let { SortBy.valueOf(it.uppercase()) },
                order = Direction.DESC.takeIf { sort?.startsWith("-") == true } ?: Direction.ASC,
            ),
        )
        return ResponseEntity.ok(
            GetAllTodos200Response(
                todos = result.get().map { TodoConverter.INSTANCE.entityToModel(it) }.toList(),
                pagination = Pagination(
                    total = result.totalElements,
                    offset = result.pageable.offset,
                    limit = result.pageable.pageSize.toLong(),
                ),
            ),
        )
    }

    override fun getTodo(id: Long): ResponseEntity<Todo> {
        logger.info("Got request to fetch todo {}", id)
        return ResponseEntity.ok(TodoConverter.INSTANCE.entityToModel(service.findById(id)))
    }

    override fun updateTodo(id: Long, todo: Todo): ResponseEntity<Todo> {
        logger.info("Got request to update todo {} with {}", id, todo)
        val updatedTodo = service.updateTodo(TodoConverter.INSTANCE.modelToDto(todo).copy(id = id))
        return ResponseEntity.ok(TodoConverter.INSTANCE.entityToModel(updatedTodo))
    }
}
