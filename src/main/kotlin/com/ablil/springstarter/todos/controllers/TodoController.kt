package com.ablil.springstarter.todos.controllers

import com.ablil.springstarter.common.converter
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

const val DEFAULT_PAGINATION_OFFSET = 0
const val DEFAULT_PAGINATION_LIMIT = 50

@RestController
class TodoController(val service: TodoService) : TodoApi {
    val logger by logger()
    val converter by converter(TodoConverter::class.java)

    override fun createTodo(todo: Todo): ResponseEntity<Todo> {
        logger.info("Got request to create todo {}", todo)
        val createTodo = service.createTodo(converter.modelToDto(todo))
        return ResponseEntity
            .created(URI("/api/todos/${createTodo.id}"))
            .body(converter.entityToModel(createTodo))
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
        tags: String?,
        status: Status?,
    ): ResponseEntity<GetAllTodos200Response> {
        logger.info(
            "Got request to filter todos: offset={}, limit={}, keyword={}, status={}, tags={}",
            offset,
            limit,
            keyword,
            status,
            tags,
        )
        val result = service.findAll(
            FiltersDto(
                offset = offset ?: DEFAULT_PAGINATION_OFFSET,
                limit = limit ?: DEFAULT_PAGINATION_LIMIT,
                keyword = keyword,
                status = if (status == null) null else TodoStatus.valueOf(status.name),
                sortBy = sort?.trim('-', '+')?.let { SortBy.valueOf(it.uppercase()) },
                order = Direction.DESC.takeIf { sort?.startsWith("-") == true } ?: Direction.ASC,
                tags = tags?.split(","),
            ),
        )
        return ResponseEntity.ok(
            GetAllTodos200Response(
                todos = result.get().map { converter.entityToModel(it) }.toList(),
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
        return ResponseEntity.ok(converter.entityToModel(service.findById(id)))
    }

    override fun updateTodo(id: Long, todo: Todo): ResponseEntity<Todo> {
        logger.info("Got request to update todo {} with {}", id, todo)
        val updatedTodo = service.updateTodo(converter.modelToDto(todo).copy(id = id))
        return ResponseEntity.ok(converter.entityToModel(updatedTodo))
    }
}
