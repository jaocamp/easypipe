package com.easypipe.examples.business.pipeline

import com.easypipe.examples.business.model.BillPaymentContext
import com.easypipe.examples.business.step.AuthorizeBillStep
import com.easypipe.examples.business.step.EmitReceiptStep
import com.easypipe.examples.business.step.ValidateBillStep
import com.easypipe.starter.business.Pipeline
import com.easypipe.starter.business.PipelineExecutor
import com.easypipe.starter.repository.PipelineExecutionRepository
import com.easypipe.starter.repository.StepExecutionRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BillPaymentPipelineDefinition {

    @Bean
    fun billPaymentPipeline(
        stepRepository: StepExecutionRepository,
        pipelineRepository: PipelineExecutionRepository
    ): Pipeline<BillPaymentContext> {
        return Pipeline(
            name = "BILL_PAYMENT",
            steps = listOf(
                ValidateBillStep(),
                AuthorizeBillStep(),
                EmitReceiptStep()
            ),
            executionMode = PipelineExecutor.Mode.SEQUENTIAL,
            autoConfirm = false,
            stepRepository = stepRepository,
            pipelineRepository = pipelineRepository
        )
    }
}