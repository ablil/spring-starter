package com.ablil.springstarter.security

import com.ablil.springstarter.persistence.entities.AccountStatus
import com.ablil.springstarter.persistence.repositories.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails {
        return userRepository.findByUsername(requireNotNull(username))?.let {
            User.withUsername(username)
                .password(it.password)
                .disabled(it.status == AccountStatus.INACTIVE)
                .accountLocked(false)
                .accountExpired(false)
                .authorities(emptyList())
                .build()
        } ?: throw UsernameNotFoundException("user $username not found")
    }
}
