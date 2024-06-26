package com.ablil.springstarter.todos.services

import com.ablil.springstarter.ResourceNotFound
import com.ablil.springstarter.common.converter
import com.ablil.springstarter.common.logger
import com.ablil.springstarter.common.persistence.OffsetPageable
import com.ablil.springstarter.todos.converters.TagConverter
import com.ablil.springstarter.todos.converters.TodoConverter
import com.ablil.springstarter.todos.dtos.FiltersDto
import com.ablil.springstarter.todos.dtos.SortBy
import com.ablil.springstarter.todos.dtos.TodoDto
import com.ablil.springstarter.todos.entities.Fields
import com.ablil.springstarter.todos.entities.TagEntity
import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.entities.TodoStatus
import com.ablil.springstarter.todos.repositories.TagRepository
import com.ablil.springstarter.todos.repositories.TodoRepository
import com.ablil.springstarter.webapi.model.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class TodoService(
    val repository: TodoRepository,
    val tagRepository: TagRepository,
    var authenticatedUser: UserDetails,
) {
    val logger by logger()
    val tagConverter by converter(TagConverter::class.java)

    fun createTodo(dto: TodoDto): TodoEntity {
        val todo = TodoConverter.INSTANCE.dtoToEntity(dto).apply {
            tags?.forEach { tag -> tag.todo = this }
        }
        return repository.saveAndFlush(todo)
    }

    fun updateTodo(dto: TodoDto): TodoEntity {
        val todo = fetchTodo(requireNotNull(dto.id))
        return repository.save(
            todo.copy(
                id = todo.id,
                title = dto.title,
                content = dto.content,
                status = dto.status?.let { TodoStatus.valueOf(it) } ?: TodoStatus.PENDING,
                tags = dto.tags?.map { tag -> tagConverter.dtoToEntity(tag) },
            ).apply {
                createdBy = todo.createdBy
                createdAt = todo.createdAt
                updatedBy = todo.updatedBy
                updatedAt = OffsetDateTime.now()
                tags?.forEach { tag -> tag.todo = this }
            },
        )
    }

    private fun fetchTodo(id: Long): TodoEntity {
        val todo = repository.findById(id).orElseThrow { ResourceNotFound("todo", id.toString()) }

        if (todo.createdBy != authenticatedUser.username) {
            logger.debug("Todo {} not owned by user {}", id, authenticatedUser.username)
            throw ResourceNotFound("todo", id.toString())
        }
        return todo
    }

    private fun fetchTag(id: Long): TagEntity {
        return tagRepository.findById(id).orElseThrow { ResourceNotFound("tag", id.toString()) }
    }

    fun findById(id: Long): TodoEntity {
        return fetchTodo(id)
    }

    fun deleteTodoById(id: Long) {
        repository.delete(fetchTodo(id))
    }

    fun findAll(filters: FiltersDto): Page<TodoEntity> {
        logger.debug("Filtering todo by {}", filters)
        val specification: Specification<TodoEntity> = convertFiltersToSpecification(filters)
        val pageable: Pageable = convertFiltersToPageable(filters)
        return repository.findAll(specification, pageable)
    }

    private fun convertFiltersToSpecification(filters: FiltersDto): Specification<TodoEntity> {
        val createdBy: Specification<TodoEntity> = Specification { root, _, builder ->
            builder.equal(root.get<String>(Fields.CREATED_BY), authenticatedUser.username)
        }

        val havingStatus = Specification<TodoEntity> { root, _, builder ->
            builder.equal(root.get<TodoStatus>(Fields.STATUS), filters.status)
        }.takeIf { filters.status != null }

        val titleContainsKeyword: Specification<TodoEntity>? = Specification<TodoEntity> { root, _, builder ->
            builder.like(root.get(Fields.TITLE), "%${filters.keyword}%")
        }.takeIf { !filters.keyword.isNullOrBlank() }

        val contentContainsKeyword: Specification<TodoEntity>? = Specification<TodoEntity> { root, _, builder ->
            builder.like(root.get(Fields.CONTENT), "%${filters.keyword}%")
        }.takeIf { !filters.keyword.isNullOrBlank() }

        return Specification.allOf(
            listOfNotNull(
                createdBy,
                havingStatus,
                titleContainsKeyword?.or(contentContainsKeyword),
            ),
        )
    }

    private fun convertFiltersToPageable(filters: FiltersDto): Pageable {
        return OffsetPageable(
            filters.offset,
            filters.limit,
            Sort.by(Sort.Order(filters.order, filters.sortBy?.value ?: SortBy.ID.value)),
        )
    }

    fun addTag(todoId: Long, tag: Tag): TagEntity {
        val todo = fetchTodo(todoId)
        return tagRepository.save(tagConverter.dtoToEntity(tag).apply { this.todo = todo })
    }

    fun deleteTag(tagId: Long) {
        tagRepository.delete(fetchTag(tagId))
    }

    fun updateTag(tagId: Long, tag: Tag): TagEntity {
        val original = fetchTag(tagId)
        return tagRepository.save(original.copy(tag = tag.tag).apply { todo = original.todo })
    }
}
