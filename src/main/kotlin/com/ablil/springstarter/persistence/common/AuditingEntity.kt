package com.ablil.springstarter.persistence.common

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class AuditingEntity {
    @CreatedDate
    @Column(updatable = false, nullable = false)
    lateinit var createdAt: Instant

    @CreatedDate
    @Column(updatable = false, nullable = false)
    lateinit var createdBy: String

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedAt: Instant

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updatedBy: String
}
