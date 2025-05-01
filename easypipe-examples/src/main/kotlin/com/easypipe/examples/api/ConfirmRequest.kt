package com.easypipe.examples.api

data class ConfirmRequest(
    val accountId: String,
    val amount: Long,
    val billNumber: String
)