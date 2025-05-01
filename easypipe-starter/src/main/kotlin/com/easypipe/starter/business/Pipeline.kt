package com.easypipe.starter.business

import com.easypipe.starter.repository.PipelineExecutionRepository
import com.easypipe.starter.repository.StepExecutionRepository
import java.util.*

class Pipeline<T>(
    val name: String,
    steps: List<Step<T>>,
    executionMode: PipelineExecutor.Mode,
    autoConfirm: Boolean,
    stepRepository: StepExecutionRepository,
    pipelineRepository: PipelineExecutionRepository
) {
    enum class Status {
        PROCESSING,
        PROCESSED,
        CONFIRMED,
        CANCELED,
        FAILED
    }

    private val executor: PipelineExecutor<T> = when (executionMode) {
        PipelineExecutor.Mode.SEQUENTIAL -> SequentialPipelineExecutor(steps, name, autoConfirm, stepRepository, pipelineRepository)
        PipelineExecutor.Mode.PARALLEL -> ParallelPipelineExecutor(steps, name, autoConfirm, stepRepository, pipelineRepository)
    }

    fun run(context: T): UUID = executor.run(context)
    fun confirm(executionId: UUID, context: T) = executor.confirm(executionId, context)
    fun cancel(executionId: UUID, context: T) = executor.cancel(executionId, context)
}