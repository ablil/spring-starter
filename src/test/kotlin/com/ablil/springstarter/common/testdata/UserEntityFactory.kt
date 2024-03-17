package com.ablil.springstarter.common.testdata

import com.ablil.springstarter.users.entities.AccountStatus
import com.ablil.springstarter.users.entities.UserEntity
import com.ablil.springstarter.users.entities.UserRole

object UserEntityFactory {
    fun newUser() = UserEntity(
        id = null,
        username = "johndoe",
        email = "johndoe@exmaple.com",
        password = "supersecurepassword",
        role = UserRole.DEFAULT,
        status = AccountStatus.INACTIVE,
        token = null,
    )
}
