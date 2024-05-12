package com.ablil.springstarter.common.persistence

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class ListToStringConverter : AttributeConverter<List<String>, String> {
    override fun convertToDatabaseColumn(items: List<String>?): String {
        return items?.map { it.trim() }?.distinct()?.joinToString(separator = ",") ?: ""
    }

    override fun convertToEntityAttribute(str: String?): List<String> {
        return str?.split(",") ?: emptyList()
    }
}
