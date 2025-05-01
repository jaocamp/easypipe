package com.easypipe.starter.helper

import com.easypipe.starter.business.RollbackableStep
import com.easypipe.starter.business.Step
import java.util.*

data class Context(
    val executedSteps: MutableList<String> = Collections.synchronizedList(mutableListOf()),
    var wasRolledBack: Boolean = false,
    var wasConfirmed: Boolean = false
)

class SuccessfulStep(private val label: String) : Step<Context> {
    override val name = label

    override fun run(context: Context): Context {
        context.executedSteps.add("run:$label")
        return context
    }

    override fun confirm(context: Context): Context {
        context.wasConfirmed = true
        context.executedSteps.add("confirm:$label")
        return context
    }
}

class FailingStep(private val label: String) : Step<Context> {
    override val name = label

    override fun run(context: Context): Context {
        context.executedSteps.add("run:$label")
        throw IllegalStateException("Step $label failed")
    }

    override fun confirm(context: Context) = context
}

class RollbackingStep(private val label: String) : RollbackableStep<Context> {
    override val name = label

    override fun run(context: Context): Context {
        context.executedSteps.add("run:$label")
        return context
    }

    override fun rollback(context: Context): Context {
        context.executedSteps.add("rollback:$label")
        context.wasRolledBack = true
        return context
    }

    override fun confirm(context: Context): Context {
        context.wasConfirmed = true
        return context
    }
}