package com.ablil.springstarter.todos.repositories

import com.ablil.springstarter.todos.entities.TagEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<TagEntity, Long>
