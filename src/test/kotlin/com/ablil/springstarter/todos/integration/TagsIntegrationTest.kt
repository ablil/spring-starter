package com.ablil.springstarter.todos.integration

import com.ablil.springstarter.common.BaseIntegrationTest
import com.ablil.springstarter.common.JpaTestConfiguration
import com.ablil.springstarter.common.testdata.TodoEntityFactory
import com.ablil.springstarter.todos.entities.TagEntity
import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.repositories.TodoRepository
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.*

@SpringBootTest
@ContextConfiguration(initializers = [BaseIntegrationTest.Initializer::class], classes = [JpaTestConfiguration::class])
@AutoConfigureMockMvc
@WithMockUser("johndoe")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TagsIntegrationTest {
    @Autowired
    lateinit var repository: TodoRepository

    @Autowired
    lateinit var mvc: MockMvc

    lateinit var todo: TodoEntity

    @BeforeAll
    fun setup() {
        todo = repository.save(
            TodoEntityFactory.random().copy(tags = listOf(TagEntity(tag = "foo"), TagEntity(tag = "bar"))),
        )
    }

    @Test
    fun `get tags of todo`() {
        mvc.get("/api/todos/${todo.id}/tags").andExpectAll {
            status { isOk() }
            jsonPath("$") { isArray() }
            jsonPath("$.length()") { value(Matchers.greaterThan(0)) }
            jsonPath("$[0].id") { isNotEmpty() }
            jsonPath("$[0].tag") { isNotEmpty() }
        }
    }

    @Test
    fun `get tags of non existing todo`() {
        mvc.get("/api/todos/1337/tags").andExpect { status { isNotFound() } }
    }

    @Test
    fun `create tag for todo`() {
        mvc.post("/api/todos/${todo.id}/tags") {
            contentType = MediaType.APPLICATION_JSON
            content = """ { "tag": "custom tag" } """.trimIndent()
        }.andExpectAll {
            status { isCreated() }
            jsonPath("$.id") { isNotEmpty() }
            jsonPath("$.tag") { value("custom tag") }
        }
    }

    @Test
    fun `create tag for non existing todo`() {
        mvc.post("/api/todos/1337/tags") {
            contentType = MediaType.APPLICATION_JSON
            content = """ { "tag": "custom tag" } """.trimIndent()
        }.andExpect { status { isNotFound() } }
    }

    @Test
    fun `update tag`() {
        val tagId = todo.tags?.first()?.id
        mvc.put("/api/tags/$tagId") {
            contentType = MediaType.APPLICATION_JSON
            content = """ { "tag": "updated content" } """.trimIndent()
        }.andExpectAll {
            status { isOk() }
            jsonPath("$.tag") { value("updated content") }
        }
    }

    @Test
    fun `update non existing tag`() {
        mvc.put("/api/tags/1337") {
            contentType = MediaType.APPLICATION_JSON
            content = """ { "tag": "updated content" } """.trimIndent()
        }.andExpect { status { isNotFound() } }
    }

    @Test
    fun `delete tag`() {
        val tagId = todo.tags?.first()?.id
        mvc.delete("/api/tags/$tagId").andExpect { status { isNoContent() } }
    }

    @Test
    fun `delete non existing tag`() {
        mvc.delete("/api/tags/1337").andExpect { status { isNotFound() } }
    }
}
