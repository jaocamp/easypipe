package com.easypipe.starter.business

import com.easypipe.starter.model.StepExecution
import com.easypipe.starter.repository.PipelineExecutionRepository
import com.easypipe.starter.repository.StepExecutionRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import java.time.Instant
import java.util.*

class ParallelPipelineExecutor<T>(
    private val steps: List<Step<T>>,
    private val name: String,
    private val autoConfirm: Boolean,
    private val stepRepository: StepExecutionRepository,
    private val pipelineRepository: PipelineExecutionRepository
) : PipelineExecutor<T> {

    private val logger = KotlinLogging.logger {}

    override fun run(context: T): UUID {
        val executionId = UUID.randomUUID()
        logger.info { "Pipeline [$name] - Starting execution $executionId (parallel)" }

        val start = Instant.now()
        pipelineRepository.insert(executionId, name, Pipeline.Status.PROCESSING, context, start)

        val results = steps.map { step ->
            CoroutineScope(Dispatchers.Default).async {
                try {
                    step.run(context)
                    stepRepository.insert(
                        StepExecution(
                            id = UUID.randomUUID(),
                            pipelineExecutionId = executionId,
                            name = step.name,
                            status = Step.Status.PROCESSED,
                            contextSnapshot = context,
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
                            contextSnapshot = context,
                            createdAt = Instant.now()
                        )
                    )
                    throw e
                }
            }
        }

        try {
            runBlocking {
                results.awaitAll()
            }
        } catch (e: Exception) {
            logger.error(e) { "Pipeline [$name] - Execution failed in parallel" }
            pipelineRepository.updateStatus(executionId, Pipeline.Status.FAILED)
            cancel(executionId, context)
            throw e
        }

        val finalStatus = if (autoConfirm) Pipeline.Status.CONFIRMED else Pipeline.Status.PROCESSED
        pipelineRepository.updateStatus(executionId, finalStatus)

        return executionId
    }

    override fun confirm(executionId: UUID, context: T) {
        logger.info { "Pipeline [$name] - Confirming execution $executionId (parallel)" }

        runBlocking {
            steps.map { step ->
                async {
                    step.confirm(context)
                    stepRepository.insert(
                        StepExecution(
                            id = UUID.randomUUID(),
                            pipelineExecutionId = executionId,
                            name = step.name,
                            status = Step.Status.CONFIRMED,
                            contextSnapshot = context,
                            createdAt = Instant.now()
                        )
                    )
                }
            }.awaitAll()
        }

        pipelineRepository.updateStatus(executionId, Pipeline.Status.CONFIRMED)
    }

    override fun cancel(executionId: UUID, context: T) {
        logger.info { "Pipeline [$name] - Canceling execution $executionId (parallel)" }

        val executedSteps = stepRepository.findLastByExecution(executionId)
            .filter { it.status == Step.Status.PROCESSED || it.status == Step.Status.CONFIRMED }

        runBlocking {
            executedSteps.mapNotNull { record ->
                val step = steps.find { it.name == record.name }
                if (step is RollbackableStep<T>) {
                    async {
                        step.rollback(context)
                        stepRepository.insert(
                            StepExecution(
                                id = UUID.randomUUID(),
                                pipelineExecutionId = executionId,
                                name = step.name,
                                status = Step.Status.REVERSED,
                                contextSnapshot = context,
                                createdAt = Instant.now()
                            )
                        )
                    }
                } else null
            }.awaitAll()
        }

        pipelineRepository.updateStatus(executionId, Pipeline.Status.CANCELED)
    }
}
