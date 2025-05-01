package com.easypipe.examples.api

data class CancelRequest(
    val accountId: String,
    val amount: Long,
    val billNumber: String
)