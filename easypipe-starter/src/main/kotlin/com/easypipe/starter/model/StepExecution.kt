package com.easypipe.starter.model

import com.easypipe.starter.business.Step
import java.time.Instant
import java.util.*

data class StepExecution<T>(
    val id: UUID,
    val pipelineExecutionId: UUID,
    val name: String,
    val status: Step.Status,
    val contextSnapshot: T,
    val createdAt: Instant
)