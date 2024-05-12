package com.ablil.springstarter.common.persistence

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
abstract class AuditingEntity {
    @CreatedDate
    @Column(updatable = false, nullable = false)
    lateinit var createdAt: OffsetDateTime

    @CreatedBy
    @Column(updatable = false, nullable = false)
    lateinit var createdBy: String

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: OffsetDateTime

    @LastModifiedBy
    @Column(nullable = false)
    lateinit var updatedBy: String
}
