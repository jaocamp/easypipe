package com.easypipe.starter.repository

import com.easypipe.starter.business.Pipeline
import java.time.Instant
import java.util.*

interface PipelineExecutionRepository {
    fun <T> insert(id: UUID, pipelineName: String, status: Pipeline.Status, contextSnapshot: T, createdAt: Instant)
    fun updateStatus(id: UUID, status: Pipeline.Status)
}