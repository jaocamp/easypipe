# EasyPipe Documentation

This document provides an in-depth overview of EasyPipe's structure, behavior, and how to use it effectively within your Spring Boot application.

---

## 📌 Core Concepts

### Pipeline
A pipeline represents a transactional process composed of ordered steps. It can run **sequentially** or **in parallel**, and optionally supports **auto-confirmation** or **manual confirmation**.

### Step
A unit of work within a pipeline. Each step can:
- Run
- Optionally support rollback (via `RollbackableStep` interface)
- Optionally support confirmation (if not auto-confirmed)

---

## 🔁 Execution Flow

### Pipeline Statuses
- `CREATED`: Pipeline was defined but not executed yet
- `PROCESSING`: Currently running steps
- `FAILED`: At least one step failed
- `PROCESSED`: Steps finished successfully but not yet confirmed
- `CONFIRMED`: Confirmed execution (if required)
- `CANCELED`: Steps rolled back

### Step Statuses
- `PROCESSED`: Executed successfully
- `CONFIRMED`: Confirmed externally
- `FAILED`: Failed to execute
- `REVERSED`: Rolled back successfully

---

## ⚙️ Configuration

### YAML
Enable EasyPipe in your `application.yml`:

```yaml
easypipe:
  enabled: true
```

### Spring Boot Auto-Config
The starter auto-configures:
- `PipelineExecutionRepository`
- `StepExecutionRepository`

These are injected into your custom pipeline beans.

---

## 📘 Example: Bill Payment Pipeline

### Context Class
```kotlin
data class BillPaymentContext(
    val billId: String,
    val amountInCents: Long,
    val authorized: Boolean = false,
    val receiptNumber: String? = null
)
```

### Steps
Each step implements `Step<T>` or `RollbackableStep<T>`.

### Pipeline Definition
```kotlin
@Bean
fun billPaymentPipeline(...) = Pipeline(
    name = "BILL_PAYMENT",
    steps = listOf(...),
    executionMode = ExecutionMode.SEQUENTIAL,
    autoConfirm = false,
    ...
)
```

### Controller
```kotlin
@RestController
class PipelineController(
    private val pipeline: Pipeline<BillPaymentContext>
) {
    @PostMapping("/run")
    fun run(@RequestBody context: BillPaymentContext): Map<String, UUID> {
        val id = pipeline.run(context)
        return mapOf("executionId" to id)
    }

    @PostMapping("/confirm/{id}")
    fun confirm(@PathVariable id: UUID, @RequestBody context: BillPaymentContext) =
        pipeline.confirm(id, context)

    @PostMapping("/cancel/{id}")
    fun cancel(@PathVariable id: UUID, @RequestBody context: BillPaymentContext) =
        pipeline.cancel(id, context)
}
```

---

## 🧪 Persistence Strategy
- Insert-only for historical audit trail
- Each step's execution saved with status + context
- JSONB used for context data

### Tables
- `pipeline_execution`
- `step_execution`

See `V1__...sql` and `V2__...sql` Flyway migrations.

---

## 💡 Best Practices
- Define context as immutable (`data class`)
- Use UUIDs for pipeline/step IDs
- Avoid business logic in step definitions — delegate to services
- Use `rollback` and `confirm` only if needed

---

## 📈 Observability
EasyPipe is designed to integrate with tracing systems like Datadog, OpenTelemetry, or Prometheus. Each step is a natural tracing boundary.

---

## 🧠 Tips
- Use parallel pipelines only when steps are independent
- Use auto-confirmation when you don’t need external post-processing
- Always validate context early in the first step

---

For support, see [README.md](./README.md) or open an issue on GitHub.
