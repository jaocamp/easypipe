package com.easypipe.starter.repository

import com.easypipe.starter.model.StepExecution
import java.util.*

interface StepExecutionRepository {
    fun <T> insert(record: StepExecution<T>)
    fun findLastByExecution(pipelineExecutionId: UUID): List<StepExecution<*>>
}