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
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import java.util.*

class SequentialPipelineExecutorTest : FunSpec({

    val stepRepository = mockk<StepExecutionRepository>(relaxed = true)
    val pipelineRepository = mockk<PipelineExecutionRepository>(relaxed = true)

    beforeTest {
        clearAllMocks()
    }

    test("should execute all steps and confirm when autoConfirm is true") {
        val steps = listOf(
            SuccessfulStep("STEP-1"),
            SuccessfulStep("STEP-2")
        )

        val pipeline = SequentialPipelineExecutor(
            steps = steps,
            name = "SEQ_AUTO",
            autoConfirm = true,
            stepRepository = stepRepository,
            pipelineRepository = pipelineRepository
        )

        val context = Context()

        val id = pipeline.run(context)

        verify { pipelineRepository.insert(id, "SEQ_AUTO", Pipeline.Status.PROCESSING, context, any()) }
        verify(exactly = 2) { stepRepository.insert(ofType(StepExecution::class)) }
        verify { pipelineRepository.updateStatus(id, Pipeline.Status.CONFIRMED) }

        context.executedSteps shouldContainExactly listOf("run:STEP-1", "run:STEP-2")
        context.wasConfirmed shouldBe false
        context.wasRolledBack shouldBe false
    }

    test("should stop execution and rollback previous steps on failure") {
        val steps = listOf(
            RollbackingStep("STEP-1"),
            FailingStep("STEP-2")
        )

        val pipeline = SequentialPipelineExecutor(
            steps = steps,
            name = "SEQ_FAIL",
            autoConfirm = false,
            stepRepository = stepRepository,
            pipelineRepository = pipelineRepository
        )

        val context = Context()

        // Mock retorno de findLastByExecution para simular step executado com sucesso
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

        context.executedSteps shouldContainExactly listOf("run:STEP-1", "run:STEP-2", "rollback:STEP-1")
        context.wasConfirmed shouldBe false
        context.wasRolledBack shouldBe true
    }

    test("should confirm all steps when confirm() is called explicitly") {
        val steps = listOf(
            SuccessfulStep("STEP-1"),
            SuccessfulStep("STEP-2")
        )

        val pipeline = SequentialPipelineExecutor(
            steps = steps,
            name = "SEQ_CONFIRM",
            autoConfirm = false,
            stepRepository = stepRepository,
            pipelineRepository = pipelineRepository
        )

        val context = Context()
        val executionId = pipeline.run(context)

        // Clear mocks to focus on confirmation assertions
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