package com.takahiro310.totpexample.service

import com.takahiro310.totpexample.model.Account

interface AccountService {
    fun createAccount(userId: String, email: String, password: String): String
    fun verifyEmail(token: String): Account
    fun getQRCode(account: Account): String
}
