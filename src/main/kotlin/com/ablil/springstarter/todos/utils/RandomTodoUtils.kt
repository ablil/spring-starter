package com.ablil.springstarter.todos.utils

import com.ablil.springstarter.todos.entities.TagEntity
import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.entities.TodoStatus
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomUtils.nextBoolean
import org.apache.commons.lang3.RandomUtils.nextInt

object RandomTodoUtils {
    fun random(): TodoEntity = TodoEntity(
        id = null,
        title = RandomStringUtils.randomPrint(20),
        content = RandomStringUtils.randomAlphabetic(200),
        status = TodoStatus.DONE.takeIf { nextBoolean() } ?: TodoStatus.PENDING,
        tags = List(nextInt(1, 5)) { RandomTagUtils.random() },
    ).apply { tags?.forEach { tag -> tag.todo = this } }

    fun random(tags: List<TagEntity>): TodoEntity =
        random().copy(tags = tags).apply { tags.forEach { tag -> tag.todo = this } }

    fun randomList(count: Int): Collection<TodoEntity> = List(count) { random() }

    fun randomList(count: Int, tags: List<TagEntity>): Collection<TodoEntity> = List(count) { random(tags) }
}
