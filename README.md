![Build](https://github.com/jaocamp/easypipe/actions/workflows/ci.yml/badge.svg)
![Starter Coverage](https://codecov.io/gh/jaocamp/easypipe/branch/main/graph/badge.svg)
![License](https://img.shields.io/github/license/jaocamp/easypipe)

# EasyPipe

**EasyPipe** is a Kotlin-based Spring Boot library for building robust, observable, and pluggable transactional pipelines.

It helps you define and execute ordered workflows composed of reusable steps with support for:

- Sequential and parallel step execution
- Step rollback and confirmation logic
- Insert-only persistence with PostgreSQL
- Explicit status tracking (CREATED, PROCESSING, FAILED, PROCESSED, CONFIRMED, CANCELED)
- Plug-and-play Spring Boot starter integration

---

## 🚀 Getting Started

### Add the dependency

Coming soon to Maven Central. For now, use a local `mavenLocal()` repo or install manually:

```kotlin
dependencies {
    implementation("com.easypipe:easypipe-starter:<version>")
}
```

---

## 📦 Example Usage

```kotlin
val pipeline = Pipeline(
    name = "BILL_PAYMENT",
    steps = listOf(
        ValidateBillStep(),
        AuthorizeBillStep(),
        EmitReceiptStep()
    ),
    executionMode = ExecutionMode.SEQUENTIAL,
    autoConfirm = false,
    stepRepository = stepRepository,
    pipelineRepository = pipelineRepository
)

val context = BillPaymentContext(billId = "123", amountInCents = 1000)
val executionId = pipeline.run(context)
```

---

## 🧠 Features

- 🔁 Reversible steps with rollback support
- 📊 Audit-safe insert-only persistence
- ☁️ PostgreSQL + JSONB support out of the box
- ⚙️ Fully configurable via Spring YAML or Java/Kotlin Beans
- 🔐 Fail-safe: each step's execution is persisted
- 📈 Ready for tracing & observability layers

---

## 📚 Documentation

See [DOCUMENTATION.md](./DOCUMENTATION.md) for full reference.

---

## 🧑‍💻 Contributing

PRs and feature suggestions are welcome!
See [CONTRIBUTING.md](./CONTRIBUTING.md) for guidelines.

---

## 📄 License

[MIT License](./LICENSE)

---

## 👤 Author

Built and maintained by [João Campos](https://github.com/jaocamp).

Feel free to reach out or star the repo if you find it useful! ⭐
