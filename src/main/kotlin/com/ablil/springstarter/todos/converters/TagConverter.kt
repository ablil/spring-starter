package com.ablil.springstarter.todos.converters

import com.ablil.springstarter.todos.entities.TagEntity
import com.ablil.springstarter.webapi.model.Tag
import org.mapstruct.Mapper

@Mapper
interface TagConverter {
    fun entityToDto(entity: TagEntity): Tag

    fun dtoToEntity(dto: Tag): TagEntity
}
