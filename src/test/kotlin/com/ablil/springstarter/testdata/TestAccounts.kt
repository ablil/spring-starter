package com.ablil.springstarter.testdata

import com.ablil.springstarter.domain.accounts.Account
import com.ablil.springstarter.domain.accounts.AccountStatus

object TestAccounts {

    val activeUser: Account = Account(
        id = null,
        username = "joedoe",
        password = "supersecretpassword",
        firstName = "joe",
        lastName = "doe",
        status = AccountStatus.ACTIVE
    )

    val inactiveUser = activeUser.copy(status = AccountStatus.INACTIVE)
}