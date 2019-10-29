package com.takahiro310.totpexample.security

import com.takahiro310.totpexample.model.Account
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User

data class AccountDetails(
    val account: Account
) : User(
    account.userId, account.password, AuthorityUtils
        .createAuthorityList("ROLE_USER")
)
