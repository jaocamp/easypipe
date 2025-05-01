package com.easypipe.examples.business.step

import com.easypipe.examples.business.model.BillPaymentContext
import com.easypipe.starter.business.Step


class ValidateBillStep : Step<BillPaymentContext> {
    override val name = "VALIDATE_BILL"

    override fun run(context: BillPaymentContext): BillPaymentContext {
        require(context.amount > 0) { "Invalid bill amount" }
        return context
    }

    override fun confirm(context: BillPaymentContext) = context
}