package com.ablil.springstarter.todos.services

import com.ablil.springstarter.common.JpaTestConfiguration
import com.ablil.springstarter.common.persistence.JPAConfiguration
import com.ablil.springstarter.todos.dtos.FiltersDto
import com.ablil.springstarter.todos.dtos.SortBy
import com.ablil.springstarter.todos.entities.TagEntity
import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.entities.TodoStatus
import com.ablil.springstarter.todos.repositories.TodoRepository
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.domain.Sort.Direction.DESC
import org.springframework.test.context.ContextConfiguration
import java.time.OffsetDateTime

@DataJpaTest
@ContextConfiguration(classes = [JPAConfiguration::class, JpaTestConfiguration::class, TodoService::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoFilteringIntegrationTest {
    @Autowired
    lateinit var repository: TodoRepository

    @Autowired
    lateinit var service: TodoService

    @BeforeAll
    fun setup() {
        repository.saveAll(todos)
    }

    @Test
    fun `fetch all todos given no filter`() {
        val actual = service.findAll(FiltersDto())
        assertEquals(todos.size, actual.content.size)
    }

    @ParameterizedTest
    @CsvSource("title,1", "groceries,2", "nonexisting,0")
    fun `fetch todos by keyword`(keyword: String, expected: Int) {
        val actual = service.findAll(FiltersDto(keyword = keyword))
        assertEquals(expected, actual.content.size)
    }

    @ParameterizedTest
    @CsvSource("PENDING,1", "DONE,3", "null,4")
    fun `fetch todos by status`(status: String, expected: Int) {
        val todoStatus = when (status) {
            "null" -> null
            else -> TodoStatus.valueOf(status)
        }
        val actual = service.findAll(FiltersDto(status = todoStatus))
        assertEquals(expected, actual.content.size)
    }

    @ParameterizedTest
    @EnumSource(value = Direction::class, names = ["ASC", "DESC"])
    fun `fetch todos sorted by id`(direction: Direction) {
        val actual = service.findAll(
            FiltersDto(order = direction, sortBy = SortBy.ID),
        ).content.map {
            it.id
        }
        val expected = actual.toTypedArray().also {
            it.sort()
            if (direction == DESC) {
                it.reverse()
            }
        }
        assertArrayEquals(expected, actual.toTypedArray())
    }

    @ParameterizedTest
    @EnumSource(value = Direction::class, names = ["ASC", "DESC"])
    fun `fetch todos sorted by created_at`(direction: Direction) {
        val actual =
            service.findAll(FiltersDto(order = direction, sortBy = SortBy.CREATED_AT)).content.map {
                it.createdAt
            }
        val expected = actual.toTypedArray().also {
            it.sort()
            if (direction == DESC) {
                it.reverse()
            }
        }
        assertArrayEquals(expected, actual.toTypedArray())
    }

    @ParameterizedTest
    @EnumSource(value = Direction::class, names = ["ASC", "DESC"])
    fun `fetch todos sorted by updated_at`(direction: Direction) {
        val actual =
            service.findAll(FiltersDto(order = direction, sortBy = SortBy.UPDATED_AT)).content.map {
                it.updatedAt
            }
        val expected = actual.toTypedArray().also {
            it.sort()
            if (direction == DESC) {
                it.reverse()
            }
        }
        assertArrayEquals(expected, actual.toTypedArray())
    }

    @Test
    fun `fetch todos given combined filters`() {
        val actual = service.findAll(FiltersDto(status = TodoStatus.PENDING, keyword = "groceries"))
        assertEquals(1, actual.content.size)
    }

    companion object {
        val todos = listOf(
            TodoEntity(
                title = "title",
                content = "lorem ipsum",
                status = TodoStatus.DONE,
                tags = listOf(TagEntity("foo"), TagEntity("bar")),
            ).apply {
                createdBy = "johndoe"
                updatedBy = "johndoe"
                createdAt = OffsetDateTime.parse("2024-10-10T00:00:00.00Z")
                updatedAt = OffsetDateTime.parse("2024-10-11T00:00:00.00Z")
            },
            TodoEntity(
                title = "groceries",
                content = "buy needed stuff for kitchen",
                status = TodoStatus.PENDING,
                tags = listOf(TagEntity("foo"), TagEntity("buzz")),
            ).apply {
                createdBy = "johndoe"
                updatedBy = "johndoe"
                createdAt = OffsetDateTime.parse("2024-09-10T00:00:00.00Z")
                updatedAt = OffsetDateTime.parse("2024-10-11T00:00:00.00Z")
            },
            TodoEntity(
                title = "work",
                content = "create a new jira ticket",
                status = TodoStatus.DONE,
                tags = listOf(TagEntity("jira"), TagEntity("fizz")),
            ).apply {
                createdBy = "johndoe"
                updatedBy = "johndoe"
                createdAt = OffsetDateTime.parse("2024-08-10T00:00:00.00Z")
                updatedAt = OffsetDateTime.parse("2024-10-11T00:00:00.00Z")
            },
            TodoEntity(
                title = "kitchen",
                content = "Buy all the needed groceries",
                status = TodoStatus.DONE,
                tags = emptyList(),
            ).apply {
                createdBy = "johndoe"
                updatedBy = "johndoe"
                createdAt = OffsetDateTime.parse("2024-12-10T00:00:00.00Z")
                updatedAt = OffsetDateTime.parse("2025-01-11T00:00:00.00Z")
            },
        )
    }
}
