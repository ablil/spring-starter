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
    lateinit var created: Instant

    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updated: Instant
}
