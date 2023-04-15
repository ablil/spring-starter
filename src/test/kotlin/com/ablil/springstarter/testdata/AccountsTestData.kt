package com.ablil.springstarter.testdata

import com.ablil.springstarter.domain.accounts.Account
import com.ablil.springstarter.domain.accounts.AccountStatus
import com.ablil.springstarter.domain.accounts.dtos.RegistrationRequest

object AccountsTestData {

    val activeUser: Account = Account(
        id = null,
        username = "joedoe",
        password = "supersecretpassword",
        firstName = "joe",
        lastName = "doe",
        status = AccountStatus.ACTIVE
    )

    val inactiveUser = activeUser.copy(status = AccountStatus.INACTIVE)

    val basicRegistrationRequest = RegistrationRequest(
        username = "joedoe",
        password = "supersecretpassword",
        firstName = null,
        lastName = null,
    )

    val fullRegistrationRequest = basicRegistrationRequest.copy(firstName = "joe", lastName = "doe")
}