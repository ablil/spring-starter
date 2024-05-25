package com.ablil.springstarter.common.persistence

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class OffsetPageable(private val offset: Int, private val limit: Int, private val sort: Sort) : Pageable {
    override fun getPageNumber(): Int = error("Unsupported operation")

    override fun getPageSize(): Int = limit

    override fun getOffset(): Long = offset.toLong()

    override fun getSort(): Sort = sort

    override fun next(): Pageable = OffsetPageable(offset + limit, limit, sort)

    override fun previousOrFirst(): Pageable = OffsetPageable((offset - limit).takeIf { it > 0L } ?: 0, limit, sort)

    override fun first(): Pageable = OffsetPageable(0, limit, sort)

    override fun withPage(pageNumber: Int): Pageable = error("Unsupported operation")

    override fun hasPrevious(): Boolean = offset > 0
}
