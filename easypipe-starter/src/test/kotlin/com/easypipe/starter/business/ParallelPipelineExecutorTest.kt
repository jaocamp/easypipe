package com.easypipe.starter.business

import com.easypipe.starter.helper.Context
import com.easypipe.starter.helper.FailingStep
import com.easypipe.starter.helper.RollbackingStep
import com.easypipe.starter.helper.SuccessfulStep
import com.easypipe.starter.model.StepExecution
import com.easypipe.starter.repository.PipelineExecutionRepository
import com.easypipe.starter.repository.StepExecutionRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.time.Instant
import java.util.*

class ParallelPipelineExecutorTest : FunSpec({

    val stepRepository = mockk<StepExecutionRepository>(relaxed = true)
    val pipelineRepository = mockk<PipelineExecutionRepository>(relaxed = true)

    beforeTest {
        clearAllMocks()
    }

    test("should run all steps in parallel and confirm pipeline") {
        val steps = listOf(
            SuccessfulStep("STEP-1"),
            SuccessfulStep("STEP-2")
        )

        val pipeline = ParallelPipelineExecutor(
            steps = steps,
            name = "PARALLEL_SUCCESS",
            autoConfirm = true,
            stepRepository = stepRepository,
            pipelineRepository = pipelineRepository
        )

        val context = Context()
        val executionId = pipeline.run(context)

        verify { pipelineRepository.insert(executionId, "PARALLEL_SUCCESS", Pipeline.Status.PROCESSING, context, any()) }
        verify(exactly = 2) { stepRepository.insert(ofType(StepExecution::class)) }
        verify { pipelineRepository.updateStatus(executionId, Pipeline.Status.CONFIRMED) }

        context.executedSteps shouldContainExactlyInAnyOrder listOf("run:STEP-1", "run:STEP-2")
        context.wasConfirmed shouldBe false // confirm() is not called unless via confirm() method
        context.wasRolledBack shouldBe false
    }

    test("should fail on step error and rollback processed steps") {
        val steps = listOf(
            RollbackingStep("STEP-1"),
            FailingStep("STEP-2")
        )

        val pipeline = ParallelPipelineExecutor(
            steps = steps,
            name = "PARALLEL_FAIL",
            autoConfirm = false,
            stepRepository = stepRepository,
            pipelineRepository = pipelineRepository
        )

        val context = Context()

        // Simula que STEP-1 foi executado com sucesso antes da falha
        every { stepRepository.findLastByExecution(any()) } returns listOf(
            StepExecution(
                id = UUID.randomUUID(),
                pipelineExecutionId = UUID.randomUUID(),
                name = "STEP-1",
                status = Step.Status.PROCESSED,
                contextSnapshot = context,
                createdAt = Instant.now()
            )
        )

        shouldThrow<IllegalStateException> {
            pipeline.run(context)
        }

        verify {
            stepRepository.insert(match<StepExecution<*>> { it.status == Step.Status.FAILED && it.name == "STEP-2" })
        }

        verify { pipelineRepository.updateStatus(any(), Pipeline.Status.FAILED) }

        context.executedSteps shouldContainExactlyInAnyOrder listOf("run:STEP-1", "run:STEP-2", "rollback:STEP-1")
        context.wasRolledBack shouldBe true
    }

    test("should confirm all steps when confirm() is called explicitly") {
        val steps = listOf(
            SuccessfulStep("STEP-1"),
            SuccessfulStep("STEP-2")
        )

        val pipeline = ParallelPipelineExecutor(
            steps = steps,
            name = "PARALLEL_CONFIRM",
            autoConfirm = false,
            stepRepository = stepRepository,
            pipelineRepository = pipelineRepository
        )

        val context = Context()
        val executionId = pipeline.run(context)

        // Clear mocks before confirm()
        clearMocks(stepRepository, pipelineRepository)

        pipeline.confirm(executionId, context)

        verify(exactly = 2) {
            stepRepository.insert(match<StepExecution<*>> {
                it.status == Step.Status.CONFIRMED &&
                        (it.name == "STEP-1" || it.name == "STEP-2")
            })
        }

        verify {
            pipelineRepository.updateStatus(executionId, Pipeline.Status.CONFIRMED)
        }

        context.wasConfirmed shouldBe true
    }
})