package com.ablil.springstarter.todos.services

import com.ablil.springstarter.ResourceNotFound
import com.ablil.springstarter.todos.converters.TodoConverter
import com.ablil.springstarter.todos.dtos.FiltersDto
import com.ablil.springstarter.todos.dtos.SortBy
import com.ablil.springstarter.todos.dtos.TodoDto
import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.entities.TodoStatus
import com.ablil.springstarter.todos.repositories.TodoRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class TodoService(val repository: TodoRepository, var authenticatedUser: UserDetails) {
    fun createTodo(dto: TodoDto): TodoEntity {
        return repository.save(TodoConverter.INSTANCE.dtoToEntity(dto))
    }

    fun updateTodo(dto: TodoDto): TodoEntity {
        val todo = fetchTodo(requireNotNull(dto.id))
        return repository.save(
            todo.copy(
                id = todo.id,
                title = dto.title,
                content = dto.content,
                status = dto.status?.let { TodoStatus.valueOf(it) } ?: TodoStatus.PENDING,
                tags = dto.tags,
            ).apply {
                createdBy = todo.createdBy
                createdAt = todo.createdAt
                updatedBy = todo.updatedBy
                updatedAt = OffsetDateTime.now()
            },
        )
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

    fun findAll(filters: FiltersDto): Page<TodoEntity> {
        val createdBy: Specification<TodoEntity> = Specification { root, _, builder ->
            builder.equal(root.get<String>("createdBy"), authenticatedUser.username)
        }

        val havingStatus = Specification<TodoEntity> { root, _, builder ->
            builder.equal(root.get<TodoStatus>("status"), filters.status)
        }.takeIf { filters.status != null }

        val titleContainsKeyword: Specification<TodoEntity>? = Specification<TodoEntity> { root, _, builder ->
            builder.like(root.get("title"), "%${filters.keyword}%")
        }.takeIf { !filters.keyword.isNullOrBlank() }

        val contentContainsKeyword: Specification<TodoEntity>? = Specification<TodoEntity> { root, _, builder ->
            builder.like(root.get("content"), "%${filters.keyword}%")
        }.takeIf { !filters.keyword.isNullOrBlank() }

        val combinedFilters: Specification<TodoEntity> = Specification.allOf(
            listOfNotNull(
                createdBy,
                havingStatus,
                titleContainsKeyword?.or(contentContainsKeyword),
            ),
        )
        return repository.findAll(
            combinedFilters,
            PageRequest.of(
                filters.page - 1,
                filters.size,
                Sort.by(Sort.Order(filters.order, filters.sortBy?.value ?: SortBy.ID.value)),
            ),
        )
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
