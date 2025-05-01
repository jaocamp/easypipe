package com.easypipe.starter.business

import java.util.*

interface PipelineExecutor<T> {
    fun run(context: T): UUID
    fun confirm(executionId: UUID, context: T)
    fun cancel(executionId: UUID, context: T)

    enum class Mode {
        SEQUENTIAL,
        PARALLEL
    }
}