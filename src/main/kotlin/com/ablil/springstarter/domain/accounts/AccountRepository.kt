package com.ablil.springstarter.domain.accounts

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

interface AccountRepository: CrudRepository<Account, Long> {

    fun findByUsername(username: String?): Account?
}