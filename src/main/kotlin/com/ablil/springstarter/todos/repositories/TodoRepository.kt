package com.ablil.springstarter.todos.repositories

import com.ablil.springstarter.common.persistence.BaseRepository
import com.ablil.springstarter.todos.entities.TodoEntity
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : JpaRepository<TodoEntity, Long>, BaseRepository, JpaSpecificationExecutor<TodoEntity> {
    fun findAllByCreatedBy(owner: String, pageable: Pageable): Page<TodoEntity>

    @Transactional
    @Modifying
    @Query(value = "truncate table todos", nativeQuery = true)
    override fun truncate()
}
