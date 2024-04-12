package com.ablil.springstarter.todos.controllers

import com.ablil.springstarter.ResourceNotFound
import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.entities.TodoStatus
import com.ablil.springstarter.todos.services.TodoService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.Instant

@WebMvcTest(TodoController::class)
@AutoConfigureMockMvc(addFilters = false)
class TodoControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var todoService: TodoService

    @Nested
    inner class FetchSingleTodo {
        @Test
        fun `get todo by id successfully`() {
            val todo = TodoEntity(content = "todo content", status = TodoStatus.PENDING, id = 1)
                .apply {
                    createdAt = Instant.now()
                    updatedAt = Instant.now()
                }
            whenever(todoService.findById(1)).thenReturn(todo)

            mockMvc.get("/api/todos/1").andExpectAll {
                status { isOk() }
                jsonPath("$.id") { value(1) }
                jsonPath("$.content") { value("todo content") }
                jsonPath("$.status") { value("PENDING") }
                jsonPath("$.created_at") { isNotEmpty() }
                jsonPath("$.updated_at") { isNotEmpty() }
            }
        }

        @Test
        fun `return 404 when fetching non existing todo`() {
            whenever(todoService.findById(1)).thenThrow(ResourceNotFound("todo", "1"))
            mockMvc.get("/api/todos/1").andExpectAll { status { isNotFound() } }
        }
    }

    @Test
    fun `return 201 after creating a todo`() {
        whenever(todoService.createTodo("lorem ipsum")).thenReturn(
            TodoEntity(content = "lorem ipsum", status = TodoStatus.PENDING, id = 1)
                .apply {
                    createdAt = Instant.now()
                    updatedAt = Instant.now()
                },
        )
        mockMvc.post("/api/todos") {
            contentType = MediaType.APPLICATION_JSON
            content =
                """
                {
                    "content": "lorem ipsum"
                }
                """.trimIndent()
        }.andExpectAll {
            status { isCreated() }
            jsonPath("$.id") { value(1) }
            jsonPath("$.content") { value("lorem ipsum") }
            jsonPath("$.status") { value("PENDING") }
            jsonPath("$.created_at") { isNotEmpty() }
            jsonPath("$.updated_at") { isNotEmpty() }
        }
    }

    @Nested
    inner class DeleteTodos {
        @Test
        fun `return 204 after deleting an existing todo`() {
            mockMvc.delete("/api/todos/1").andExpectAll { status { isNoContent() } }
        }

        @Test
        fun `return 404 when deleting non existing todo`() {
            whenever(todoService.deleteTodoById(1)).thenThrow(ResourceNotFound("todo", "1"))
            mockMvc.delete("/api/todos/1").andExpectAll { status { isNotFound() } }
        }
    }

    @Nested
    inner class FetchAllTodos {
        @BeforeEach
        fun beforeEach() {
            whenever(todoService.findAll(any(), any())).thenAnswer { args ->
                val page = args.getArgument<Int>(0)
                val size = args.getArgument<Int>(1)
                PageImpl(
                    (1..size).map {
                        TodoEntity(
                            content = "todo content",
                            status = TodoStatus.PENDING,
                            id = it.toLong(),
                        ).apply {
                            createdAt = Instant.now()
                            updatedAt = Instant.now()
                        }
                    },
                    PageRequest.of(page - 1, size), // pageable starts counting with 0
                    page * size + 1L,
                )
            }
        }

        @Test
        fun `get multiple todos given no query params with 200`() {
            mockMvc.get("/api/todos").andExpectAll {
                status { isOk() }
                jsonPath("$.total") { isNotEmpty() }
                jsonPath("$.page") { value(1) }
                jsonPath("$.todos") { isArray() }
            }
        }

        @Test
        fun `get multiple todos with query params 200`() {
            mockMvc.get("/api/todos?page=2&size=5").andExpectAll {
                status { isOk() }
                jsonPath("$.total") { isNotEmpty() }
                jsonPath("$.page") { value(2) }
                jsonPath("$.todos") { isArray() }
                jsonPath("$.todos.length()") { value(5) }
            }
        }

        @Test
        fun `return 400 given negative query param values`() {
            mockMvc.get("/api/todos?page=-1&size=5").andExpectAll {
                status { isBadRequest() }
            }
        }
    }
}
