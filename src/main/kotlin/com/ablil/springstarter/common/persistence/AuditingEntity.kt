package com.ablil.springstarter.common.persistence

import com.ablil.springstarter.todos.entities.Fields
import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class AuditingEntity(
    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: OffsetDateTime?,
    @CreatedBy
    @Column(name = Fields.CREATED_BY, updatable = false, nullable = false)
    var createdBy: String?,
    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: OffsetDateTime?,
    @LastModifiedBy
    @Column(nullable = false)
    var updatedBy: String?,
) {
    constructor() : this(null, null, null, null)
}
