package com.easypipe.examples.api

data class RunRequest(
    val accountId: String,
    val amount: Long,
    val billNumber: String
)