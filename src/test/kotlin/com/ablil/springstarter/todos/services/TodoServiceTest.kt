package com.ablil.springstarter.todos.services

import com.ablil.springstarter.ResourceNotFound
import com.ablil.springstarter.common.persistence.JPAConfiguration
import com.ablil.springstarter.common.testdata.TodoEntityFactory
import com.ablil.springstarter.todos.repositories.TodoRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.context.annotation.Import
import org.springframework.security.core.userdetails.UserDetails
import java.util.Optional

@ExtendWith(MockitoExtension::class)
@Import(JPAConfiguration::class)
class TodoServiceTest {
    @Mock
    lateinit var repository: TodoRepository

    @Mock
    lateinit var authenticatedUser: UserDetails

    @InjectMocks
    lateinit var todoService: TodoService

    @BeforeEach
    fun beforeEach() {
        whenever(authenticatedUser.username).thenReturn("johndoe")
    }

    @Test
    fun `throw exception when fetching non-existing todo`() {
        val todo = TodoEntityFactory.random().also { it.createdBy = "janedoe" }
        whenever(repository.findById(1)).thenReturn(Optional.of(todo))
        assertThrows<ResourceNotFound> { todoService.findById(1) }
    }
}
