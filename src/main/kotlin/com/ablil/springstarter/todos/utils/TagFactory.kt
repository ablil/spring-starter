package com.ablil.springstarter.todos.utils

import com.ablil.springstarter.todos.entities.TagEntity

object TagFactory {
    fun create(tag: String): TagEntity = TagEntity(tag = tag)
}
