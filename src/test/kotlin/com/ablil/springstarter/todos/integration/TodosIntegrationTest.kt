package com.ablil.springstarter.todos.integration

import com.ablil.springstarter.common.BaseIntegrationTest
import com.ablil.springstarter.common.IntegrationTest
import com.ablil.springstarter.common.testdata.TodoEntityFactory
import com.ablil.springstarter.todos.dtos.TodoDto
import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.repositories.TodoRepository
import com.ablil.springstarter.todos.services.TodoServiceTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.isNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.*

@IntegrationTest
@WithMockUser("johndoe")
class TodosIntegrationTest : BaseIntegrationTest() {
    @Autowired
    lateinit var todoRepository: TodoRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    @Nested
    inner class FetchAllTodos {
        @BeforeEach
        fun setup() {
            todoRepository.deleteAll()
            todoRepository.saveAll(sampleTodos)
        }

        @Test
        fun `fetch all todos given no query params`() {
            mockMvc.get("/api/todos")
                .andExpectAll {
                    status { isOk() }
                    jsonPath("$.total") { value(sampleTodos.size) }
                    jsonPath("$.page") { value(1) }
                    jsonPath("$.todos") { isArray() }
                    jsonPath("$.todos.length()") { value(20) }
                }
        }

        @Test
        fun `fetch todos given some query params`() {
            mockMvc.get("/api/todos?page=2&size=2")
                .andExpectAll {
                    status { isOk() }
                    jsonPath("$.total") { value(sampleTodos.size) }
                    jsonPath("$.page") { value(2) }
                    jsonPath("$.todos.length()") { value(2) }
                }
        }
    }

    @Nested
    inner class FetchSingleTodo {
        @Test
        fun `fetch non existing todo`() {
            mockMvc.get("/api/todos/99").andExpect { status { isNotFound() } }
        }

        @Test
        fun `fetch an existing todo`() {
            val savedTodo = saveSingleTodo(
                TodoDto(
                    title = "a title",
                    content = "lorem ipsum",
                    status = "DONE",
                ),
            )
            mockMvc.get("/api/todos/${requireNotNull(savedTodo.id)}")
                .andExpectAll {
                    status { isOk() }
                    jsonPath("$.id") { value(requireNotNull(savedTodo.id)) }
                    jsonPath("$.title") { value("a title") }
                    jsonPath("$.content") { value("lorem ipsum") }
                    jsonPath("$.status") { value("DONE") }
                }
        }
    }

    @Nested
    inner class CreateAndUpdateTodo {
        @Test
        fun `create todo given minimum attributes`() {
            mockMvc.post("/api/todos") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    { "title": "a title" }
                    """.trimIndent()
            }.andExpectAll {
                status { isCreated() }
                jsonPath("$.id") { isNotEmpty() }
                jsonPath("$.title") { value("a title") }
                jsonPath("$.content") { isNull<String>() }
                jsonPath("$.status") { value("PENDING") }
                jsonPath("$.tags") { isNull<List<String>>() }
                jsonPath("$.created_at") { isNotEmpty() }
                jsonPath("$.updated_at") { isNotEmpty() }
            }
        }

        @Test
        fun `create todo given all attributes`() {
            mockMvc.post("/api/todos") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {
                    "title": "a title",
                    "content": "lorem ipsum",
                    "status": "DONE",
                    "tags": ["foo", "bar"]
                    }
                    """.trimIndent()
            }.andExpectAll {
                status { isCreated() }
                jsonPath("$.id") { isNotEmpty() }
                jsonPath("$.title") { value("a title") }
                jsonPath("$.content") { value("lorem ipsum") }
                jsonPath("$.status") { value("DONE") }
                jsonPath("$.tags") { isArray() }
                jsonPath("$.created_at") { isNotEmpty() }
                jsonPath("$.updated_at") { isNotEmpty() }
            }
        }

        @Test
        fun `fully update todo given minimum attributes`() {
            val existingTodo =
                saveSingleTodo(
                    TodoDto(
                        "original",
                        content = "content",
                        status = "DONE",
                        tags = listOf("foo", "bar"),
                    ),
                )
            mockMvc.put("/api/todos/${existingTodo.id}") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {"title": "updated title"}
                    """.trimIndent()
            }.andExpectAll {
                status { isOk() }
                jsonPath("$.id") { isNotEmpty() }
                jsonPath("$.title") { value("updated title") }
                jsonPath("$.content") { isNull<String>() }
                jsonPath("$.status") { value("PENDING") }
                jsonPath("$.tags") { isNull<List<String>>() }
                jsonPath("$.created_at") { isNotEmpty() }
                jsonPath("$.updated_at") { isNotEmpty() }
            }
        }

        @Test
        fun `fully update todo given all attributes`() {
            val existingTodo =
                saveSingleTodo(TodoDto("original", content = "content", status = "PENDING"))
            mockMvc.put("/api/todos/${existingTodo.id}") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {
                    "title": "updated title",
                    "content": "updated lorem ipsum",
                    "status": "DONE",
                    "tags": ["foo", "bar"]
                    }
                    """.trimIndent()
            }.andExpectAll {
                status { isOk() }
                jsonPath("$.title") { value("updated title") }
                jsonPath("$.content") { value("updated lorem ipsum") }
                jsonPath("$.status") { value("DONE") }
                jsonPath("$.tags") { isArray() }
                jsonPath("$.created_at") { isNotEmpty() }
                jsonPath("$.updated_at") { isNotEmpty() }
            }
        }
    }

    @Nested
    inner class DeleteTodo {
        @Test
        fun `delete an existing todo`() {
            val savedTodo = saveSingleTodo(TodoDto("random content"))
            mockMvc.delete("/api/todos/${savedTodo.id}")
                .andExpect { status { isNoContent() } }
        }

        @Test
        fun `delete non existing todo`() {
            mockMvc.delete("/api/todo/99")
                .andExpect { status { isNotFound() } }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class FilterMultipleTodos {
        @BeforeAll
        fun setup() {
            todoRepository.saveAll(TodoServiceTest.todos)
        }

        @Test
        fun `filter todos given some filters`() {
            mockMvc.get("/api/todos?status=PENDING&keyword=groceries")
                .andExpectAll {
                    status { isOk() }
                    jsonPath("$.total") { value(1) }
                    jsonPath("$.page") { value(1) }
                    jsonPath("$.todos.length()") { value(1) }
                }
        }
    }

    private fun saveSingleTodo(dto: TodoDto): TodoEntity {
        return todoRepository.save(dto.toEntity())
    }

    companion object {
        val sampleTodos = (1..20).map {
            TodoEntityFactory.random().also {
                it.createdBy = "johndoe"
            }
        }
    }
}
