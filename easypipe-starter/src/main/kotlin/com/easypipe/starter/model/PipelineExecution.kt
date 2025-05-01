package com.easypipe.starter.model

import com.easypipe.starter.business.Pipeline
import java.time.Instant
import java.util.*

data class PipelineExecution<T>(
    val id: UUID,
    val name: String,
    val status: Pipeline.Status,
    val contextSnapshot: T,
    val createdAt: Instant
)