package com.takahiro310.totpexample.model

data class Account(
    val userId: String,
    val password: String,
    val email: String,
    val secret: String,
    val enabled: Boolean
)
