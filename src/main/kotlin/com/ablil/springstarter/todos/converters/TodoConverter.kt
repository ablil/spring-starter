package com.ablil.springstarter.todos.converters

import com.ablil.springstarter.todos.dtos.TodoDto
import com.ablil.springstarter.todos.entities.TodoEntity
import com.ablil.springstarter.todos.entities.TodoStatus
import com.ablil.springstarter.webapi.model.Status
import com.ablil.springstarter.webapi.model.Todo
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ValueMapping
import org.mapstruct.factory.Mappers

@Mapper(uses = [TagConverter::class])
@JvmDefaultWithCompatibility
interface TodoConverter {
    @Mapping(target = "status", source = "status", defaultValue = "PENDING")
    fun dtoToEntity(dto: TodoDto): TodoEntity

    fun modelToDto(todo: Todo): TodoDto

    fun entityToModel(entity: TodoEntity): Todo

    @ValueMapping(target = "PENDING", source = "PENDING")
    @ValueMapping(target = "DONE", source = "DONE")
    fun statusTosStatus(status: TodoStatus): Status

    companion object {
        val INSTANCE: TodoConverter = Mappers.getMapper(TodoConverter::class.java)
    }
}
