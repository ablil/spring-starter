package com.ablil.springstarter.utils

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

object SecurityUtil {

    fun getCurrentUser(): UserDetails = SecurityContextHolder.getContext().authentication.principal as UserDetails
}