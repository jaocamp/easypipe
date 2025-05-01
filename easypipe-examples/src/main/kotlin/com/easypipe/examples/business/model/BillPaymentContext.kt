package com.easypipe.examples.business.model

import java.util.*

data class BillPaymentContext(
    val billId: UUID,
    val amount: Long,
    val authorized: Boolean = false,
    val receiptNumber: String? = null
)