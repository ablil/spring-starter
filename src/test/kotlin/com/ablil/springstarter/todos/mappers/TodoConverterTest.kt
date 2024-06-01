package com.ablil.springstarter.todos.mappers

import com.ablil.springstarter.common.converter
import com.ablil.springstarter.todos.converters.TodoConverter
import com.ablil.springstarter.todos.dtos.TodoDto
import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.entities.TodoStatus
import com.ablil.springstarter.todos.utils.TagFactory
import com.ablil.springstarter.todos.utils.TodoFactory
import com.ablil.springstarter.webapi.model.Status
import com.ablil.springstarter.webapi.model.Tag
import com.ablil.springstarter.webapi.model.Todo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.OffsetDateTime
import java.util.stream.Stream

class TodoConverterTest {
    val converter by converter(TodoConverter::class.java)

    @ParameterizedTest
    @MethodSource("dtoToEntity")
    fun `convert dto to entity`(dto: TodoDto, entity: TodoEntity) {
        assertEquals(entity, converter.dtoToEntity(dto))
    }

    @ParameterizedTest
    @MethodSource("modelToDto")
    fun `convert api model to entity`(model: Todo, dto: TodoDto) {
        assertEquals(dto, converter.modelToDto(model))
    }

    @ParameterizedTest
    @MethodSource("entityToModel")
    fun `convert todo entity to model`(entity: TodoEntity, model: Todo) {
        assertEquals(
            model,
            converter.entityToModel(
                entity.apply {
                    createdBy = "johndoe"
                    updatedBy = "johndoe"
                    createdAt = OffsetDateTime.parse("2024-10-10T00:00:00.00Z")
                    updatedAt = OffsetDateTime.parse("2024-10-10T00:00:00.00Z")
                },
            ),
        )
    }

    companion object {
        @JvmStatic
        fun dtoToEntity(): Stream<Arguments> = Stream.of(
            Arguments.of(
                TodoDto(title = "title"),
                TodoFactory.create("title"),
            ),
            Arguments.of(
                TodoDto(
                    title = "title",
                    content = "random content",
                    status = "DONE",
                    tags = listOf(Tag("foo"), Tag("bar")),
                    id = 10,
                ),
                TodoFactory.create(
                    "title",
                    "random content",
                    TodoStatus.DONE,
                    listOf(TagFactory.create("foo"), TagFactory.create("bar")),
                ).copy(id = 10),
            ),
        )

        @JvmStatic
        fun entityToModel(): Stream<Arguments> = Stream.of(
            Arguments.of(
                TodoFactory.create("title"),
                Todo(
                    title = "title",
                    status = Status.PENDING,
                    createdAt = OffsetDateTime.parse("2024-10-10T00:00:00.00Z"),
                    updatedAt = OffsetDateTime.parse("2024-10-10T00:00:00.00Z"),
                ),
            ),
            Arguments.of(
                TodoFactory.create(
                    "title",
                    "random content",
                    TodoStatus.DONE,
                    listOf(
                        TagFactory.create("foo"),
                        TagFactory.create("bar"),
                    ),
                ).copy(id = 10),
                Todo(
                    title = "title",
                    content = "random content",
                    status = Status.DONE,
                    tags = listOf(Tag("foo"), Tag("bar")),
                    id = "10",
                    createdAt = OffsetDateTime.parse("2024-10-10T00:00:00.00Z"),
                    updatedAt = OffsetDateTime.parse("2024-10-10T00:00:00.00Z"),
                ),
            ),
        )

        @JvmStatic
        fun modelToDto(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Todo(title = "title"),
                TodoDto(title = "title"),
            ),
            Arguments.of(
                Todo(
                    title = "title",
                    content = "random content",
                    status = Status.DONE,
                    tags = listOf(Tag("foo"), Tag("foo")),
                    id = "10",
                ),
                TodoDto(
                    title = "title",
                    content = "random content",
                    status = "DONE",
                    id = 10,
                    tags = listOf(Tag("foo"), Tag("foo")),
                ),
            ),
        )
    }
}
