package com.ablil.springstarter.todos.utils

import com.ablil.springstarter.todos.entities.TagEntity
import com.ablil.springstarter.todos.entities.TodoEntity
import org.apache.commons.lang3.RandomStringUtils

object RandomTagUtils {
    fun random(): TagEntity = TagEntity(tag = RandomStringUtils.randomAlphabetic(10))

    fun random(todo: TodoEntity): TagEntity = random().apply { this.todo = todo }

    fun randomList(count: Int): Collection<TagEntity> = List(count) { random() }

    fun randomList(count: Int, todo: TodoEntity): Collection<TagEntity> = List(count) { random(todo) }
}
