package com.ablil.springstarter.security

import com.ablil.springstarter.domain.accounts.AccountRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val accountRepository: AccountRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        return accountRepository.findByUsername(username)?.let {
            User.withUsername(username)
                .password(it.password)
                .disabled(false)
                .accountLocked(false)
                .accountExpired(false)
                .authorities(emptyList())
                .build()
        } ?: throw UsernameNotFoundException("user $username does NOT exists !")
    }
}