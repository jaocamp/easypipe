package com.easypipe.examples.controller

import com.easypipe.examples.business.model.BillPaymentContext
import com.easypipe.starter.business.Pipeline
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/pipeline")
@Tag(name = "Bill Payment Pipeline")
class PipelineController(
    private val pipeline: Pipeline<BillPaymentContext>
) {

    @PostMapping("/run")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Run pipeline", description = "Starts the Bill Payment pipeline")
    fun run(@RequestBody context: BillPaymentContext): Map<String, UUID> {
        val executionId = pipeline.run(context)
        return mapOf("executionId" to executionId)
    }

    @PostMapping("/confirm/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Confirm pipeline", description = "Confirms the pipeline execution")
    fun confirm(@PathVariable id: UUID, @RequestBody context: BillPaymentContext) {
        pipeline.confirm(id, context)
    }

    @PostMapping("/cancel/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancel pipeline", description = "Cancels the pipeline execution")
    fun cancel(@PathVariable id: UUID, @RequestBody context: BillPaymentContext) {
        pipeline.cancel(id, context)
    }
}