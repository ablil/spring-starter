package com.ablil.springstarter.todos.presistence

import com.ablil.springstarter.common.JpaTestConfiguration
import com.ablil.springstarter.common.persistence.JPAConfiguration
import com.ablil.springstarter.common.persistence.ListToStringConverter
import com.ablil.springstarter.todos.repositories.TodoRepository
import com.ablil.springstarter.todos.utils.RandomTodoUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.AuditorAware
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [JPAConfiguration::class, JpaTestConfiguration::class, ListToStringConverter::class])
class TodoRepositoryTest {
    @Autowired
    lateinit var repository: TodoRepository

    @Autowired
    lateinit var auditorAware: AuditorAware<String>

    @BeforeEach
    fun setup(): Unit = repository.deleteAll()

    @Test
    fun `get paginated todos`() {
        repository.saveAll(RandomTodoUtils.randomList(20))
        val result = repository.findAllByCreatedBy(auditorAware.currentAuditor.get(), PageRequest.of(0, 10))
        val todos = result.get().toList()
        Assertions.assertEquals(10, todos.size)
        Assertions.assertEquals(20, result.totalElements)
    }
}
