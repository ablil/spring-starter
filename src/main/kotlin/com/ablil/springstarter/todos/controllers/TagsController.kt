package com.ablil.springstarter.todos.controllers

import com.ablil.springstarter.common.converter
import com.ablil.springstarter.common.logger
import com.ablil.springstarter.todos.converters.TagConverter
import com.ablil.springstarter.todos.entities.TagEntity
import com.ablil.springstarter.todos.services.TodoService
import com.ablil.springstarter.webapi.api.TagsApi
import com.ablil.springstarter.webapi.model.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class TagsController(val service: TodoService) : TagsApi {
    val logger by logger()
    val converter by converter(TagConverter::class.java)

    override fun addTags(todoId: Long, tag: Tag): ResponseEntity<Tag> {
        logger.info("Got request to add tag {} for todo {}", tag, todoId)
        val created: TagEntity = service.addTag(todoId, tag)
        return ResponseEntity.created(URI.create("/api/tags/${tag.id}")).body(converter.entityToDto(created))
    }

    override fun deleteTag(tagId: Long): ResponseEntity<Unit> {
        logger.info("Got request to delete tag {}", tagId)
        service.deleteTag(tagId)
        return ResponseEntity.noContent().build()
    }

    override fun getTags(todoId: Long): ResponseEntity<List<Tag>> {
        logger.info("Get request to fetch tags of todo {}", todoId)
        val tags = service.findById(todoId).tags?.map { converter.entityToDto(it) } ?: emptyList()
        return ResponseEntity.ok(tags)
    }

    override fun updateTag(tagId: Long, tag: Tag): ResponseEntity<Tag> {
        logger.info("Got request to update tag {} with {}", tagId, tag)
        val updated = service.updateTag(tagId, tag)
        return ResponseEntity.ok(converter.entityToDto(updated))
    }
}
