package com.easypipe.examples.api

data class PipelineResponse(
    val pipelineId: String,
    val status: String,
    val message: String
)