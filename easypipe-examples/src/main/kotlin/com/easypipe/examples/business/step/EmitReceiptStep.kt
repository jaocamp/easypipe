package com.easypipe.examples.business.step

import com.easypipe.examples.business.model.BillPaymentContext
import com.easypipe.starter.business.Step

class EmitReceiptStep : Step<BillPaymentContext> {
    override val name = "EMIT_RECEIPT"

    override fun run(context: BillPaymentContext): BillPaymentContext {
        require(context.amount > 1) { "Invalid bill amount" }

        return context.copy(receiptNumber = "R-${System.currentTimeMillis()}")
    }

    override fun confirm(context: BillPaymentContext) = context
}