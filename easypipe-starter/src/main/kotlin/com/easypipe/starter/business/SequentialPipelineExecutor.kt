package com.easypipe.starter.business

import com.easypipe.starter.model.StepExecution
import com.easypipe.starter.repository.PipelineExecutionRepository
import com.easypipe.starter.repository.StepExecutionRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import java.time.Instant
import java.util.*

class SequentialPipelineExecutor<T>(
    private val steps: List<Step<T>>,
    private val name: String,
    private val autoConfirm: Boolean,
    private val stepRepository: StepExecutionRepository,
    private val pipelineRepository: PipelineExecutionRepository
) : PipelineExecutor<T> {

    private val logger = KotlinLogging.logger {}

    override fun run(context: T): UUID {
        val executionId = UUID.randomUUID()
        logger.info { "Pipeline [$name] - Starting execution $executionId (sequential)" }

        val start = Instant.now()
        pipelineRepository.insert(executionId, name, Pipeline.Status.PROCESSING, context, start)

        var currentContext = context

        try {
            for (step in steps) {
                try {
                    currentContext = step.run(currentContext)

                    stepRepository.insert(
                        StepExecution(
                            id = UUID.randomUUID(),
                            pipelineExecutionId = executionId,
                            name = step.name,
                            status = Step.Status.PROCESSED,
                            contextSnapshot = currentContext,
                            createdAt = Instant.now()
                        )
                    )
                } catch (e: Exception) {
                    stepRepository.insert(
                        StepExecution(
                            id = UUID.randomUUID(),
                            pipelineExecutionId = executionId,
                            name = step.name,
                            status = Step.Status.FAILED,
                            contextSnapshot = currentContext,
                            createdAt = Instant.now()
                        )
                    )
                    throw e
                }
            }

            val finalStatus = if (autoConfirm) Pipeline.Status.CONFIRMED else Pipeline.Status.PROCESSED
            pipelineRepository.updateStatus(executionId, finalStatus)

        } catch (ex: Exception) {
            logger.error(ex) { "Pipeline [$name] - Execution failed" }
            pipelineRepository.updateStatus(executionId, Pipeline.Status.FAILED)
            cancel(executionId, context)
            throw ex
        }

        return executionId
    }

    override fun confirm(executionId: UUID, context: T) {
        logger.info { "Pipeline [$name] - Confirming execution $executionId" }

        var ctx = context

        steps.forEach { step ->
            ctx = step.confirm(ctx)

            stepRepository.insert(
                StepExecution(
                    id = UUID.randomUUID(),
                    pipelineExecutionId = executionId,
                    name = step.name,
                    status = Step.Status.CONFIRMED,
                    contextSnapshot = ctx,
                    createdAt = Instant.now()
                )
            )
        }

        pipelineRepository.updateStatus(executionId, Pipeline.Status.CONFIRMED)
    }

    override fun cancel(executionId: UUID, context: T) {
        logger.info { "Pipeline [$name] - Canceling execution $executionId" }

        val executedSteps = stepRepository.findLastByExecution(executionId)
            .filter { it.status == Step.Status.PROCESSED || it.status == Step.Status.CONFIRMED }
            .reversed()

        var ctx = context

        executedSteps.forEach { stepExecution ->
            val step = steps.find { it.name == stepExecution.name }
            if (step is RollbackableStep<T>) {
                ctx = step.rollback(ctx)

                stepRepository.insert(
                    StepExecution(
                        id = UUID.randomUUID(),
                        pipelineExecutionId = executionId,
                        name = step.name,
                        status = Step.Status.REVERSED,
                        contextSnapshot = ctx,
                        createdAt = Instant.now()
                    )
                )
            }
        }

        pipelineRepository.updateStatus(executionId, Pipeline.Status.CANCELED)
    }
}