package com.easypipe.examples.business.step

import com.easypipe.examples.business.model.BillPaymentContext
import com.easypipe.starter.business.RollbackableStep

class AuthorizeBillStep : RollbackableStep<BillPaymentContext> {
    override val name = "AUTHORIZE_BILL"

    override fun run(context: BillPaymentContext): BillPaymentContext {
        return context.copy(authorized = true)
    }

    override fun rollback(context: BillPaymentContext): BillPaymentContext {
        return context.copy(authorized = false)
    }

    override fun confirm(context: BillPaymentContext) = context
}