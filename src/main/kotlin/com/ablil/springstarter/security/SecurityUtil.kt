package com.ablil.springstarter.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

object SecurityUtil {

    fun getCurrentUser(): UserDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails
}
