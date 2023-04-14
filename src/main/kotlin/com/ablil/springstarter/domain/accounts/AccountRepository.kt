package com.ablil.springstarter.domain.accounts

import org.springframework.data.repository.CrudRepository

interface AccountRepository: CrudRepository<Account, Long> {

    fun findByUsername(username: String?): Account?
}