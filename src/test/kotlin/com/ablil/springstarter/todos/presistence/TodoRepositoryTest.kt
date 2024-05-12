package com.ablil.springstarter.todos.presistence

import com.ablil.springstarter.common.WithMockUserContext
import com.ablil.springstarter.common.persistence.JPAConfiguration
import com.ablil.springstarter.common.testdata.TodoEntityFactory
import com.ablil.springstarter.todos.repositories.TodoRepository
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest

@DataJpaTest
@Import(JPAConfiguration::class, WithMockUserContext::class)
class TodoRepositoryTest {
    @Autowired
    lateinit var repository: TodoRepository

    @BeforeEach
    fun setup(): Unit = repository.deleteAll()

    @Test
    fun `get paginated todos`() {
        repository.saveAll((1..20).map { TodoEntityFactory.random() })
        val result = repository.findAllByCreatedBy("johndoe", PageRequest.of(0, 10))
        val todos = result.get().toList()
        assertEquals(10, todos.size)
        assertEquals(20, result.totalElements)
    }
}
