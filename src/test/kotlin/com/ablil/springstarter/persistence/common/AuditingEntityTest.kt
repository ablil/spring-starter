package com.ablil.springstarter.persistence.common

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@RepositoryTest
class AuditingEntityTest(
    @Autowired val testEntityRepository: TestEntityRepository
) {

    @Test
    fun `fill auditing attributes`() {
        val entity = testEntityRepository.save(TestEntity())
        assertAll(
            { assertNotNull(entity.createdAt) },
            { assertNotNull(entity.createdBy) },
            { assertNotNull(entity.updatedAt) },
            { assertNotNull(entity.updatedBy) },
        )
    }
}

@Entity
class TestEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
) : AuditingEntity()

@Repository
interface TestEntityRepository : JpaRepository<TestEntity, String>
