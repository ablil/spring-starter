package com.ablil.springstarter.common

import org.mapstruct.factory.Mappers
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun <R : Any> R.logger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(this.javaClass) }
}

fun <T> converter(clazz: Class<T>): Lazy<T> {
    return lazy { Mappers.getMapper(clazz) }
}
