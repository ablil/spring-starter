package com.ablil.springstarter.todos.integration

import com.ablil.springstarter.common.BaseIntegrationTest
import com.ablil.springstarter.common.IntegrationTest
import com.ablil.springstarter.common.testdata.TodoEntityFactory
import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.repositories.TodoRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@IntegrationTest
@AutoConfigureMockMvc
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
            todoRepository.truncate()
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

        @Test
        fun `empty result list should return 404`() {
            mockMvc.get("/api/todos?=page=99")
                .andExpect { status { isNotFound() } }
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
            val savedTodo = saveSingleTodo("random content")
            mockMvc.get("/api/todos/${requireNotNull(savedTodo.id)}")
                .andExpectAll {
                    status { isOk() }
                    jsonPath("$.id") { value(requireNotNull(savedTodo.id)) }
                }
        }
    }

    @Nested
    inner class CreateAndUpdateTodo {
        @Test
        fun `create todo`() {
            mockMvc.post("/api/todos") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {"content": "randomcontent"}
                    """.trimIndent()
            }.andExpectAll {
                status { isCreated() }
                jsonPath("$.id") { exists() }
                jsonPath("$.content") { value("randomcontent") }
                jsonPath("$.status") { value("PENDING") }
            }
        }

        @Test
        fun `fully update todo`() {
            val existingTodo = saveSingleTodo("original")
            mockMvc.put("/api/todos/${existingTodo.id}") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {"content": "updated", "status": "DONE"}
                    """.trimIndent()
            }.andExpectAll {
                status { isOk() }
                jsonPath("$.content") { value("updated") }
                jsonPath("$.status") { value("DONE") }
            }
        }
    }

    @Nested
    inner class DeleteTodo {
        @Test
        fun `delete an existing todo`() {
            val savedTodo = saveSingleTodo("random content")
            mockMvc.delete("/api/todos/${savedTodo.id}")
                .andExpect { status { isNoContent() } }
        }

        @Test
        fun `delete non existing todo`() {
            mockMvc.delete("/api/todo/99")
                .andExpect { status { isNotFound() } }
        }
    }

    private fun saveSingleTodo(content: String): TodoEntity {
        return todoRepository.save(TodoEntityFactory.withContent(content))
    }

    companion object {
        val sampleTodos = (1..20).map {
            TodoEntityFactory.random().also {
                it.createdBy = "johndoe"
            }
        }
    }
}
