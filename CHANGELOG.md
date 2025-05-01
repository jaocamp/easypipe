# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]
### Added
- Configured JaCoCo for code coverage reporting in both `easypipe-starter` and `easypipe-examples`.
- Added GitHub Actions step to generate and upload coverage reports to Codecov with module-specific flags (`starter`, `examples`).
- Integrated badges in `README.md` to display separate coverage metrics per module.

## [Unreleased]
### Changed
- Disabled `bootJar` task in the `easypipe-starter` module to prevent build errors due to missing main class.
- Enabled standard `jar` packaging for the starter module.

### Removed
- Unused DTOs (`Request` / `Response`) from the `easypipe-examples` module after simplification of the controller layer.

## [0.1.0] - 2025-04-29
### Added
- Initial release of EasyPipe core.
- Support for sequential and parallel pipeline execution.
- Pipeline and step status tracking (`CREATED`, `PROCESSING`, `FAILED`, etc).
- Rollback and confirm interfaces per step.
- Insert-only persistence with PostgreSQL using JSONB.
- Configurable execution mode (`SEQUENTIAL` or `PARALLEL`).
- Optional auto-confirmation support.
- Spring Boot starter with auto-configuration.
- Step-level abstraction with support for generics.
- Test coverage using Kotest and Mockk.
- Example project demonstrating Bill Payment pipeline.
- Full Swagger API exposure (`run`, `confirm`, `cancel`).
- Runtime logging with `kotlin-logging`.
- YAML-based configuration (`easypipe.enabled`).
- Docker Compose environment for PostgreSQL setup.

### Changed
- Extracted execution logic into separate executors (`SequentialPipelineExecutor`, `ParallelPipelineExecutor`).
- Refactored `Pipeline.kt` into definition + executor structure for SOLID compliance.
- Changed execution model to use explicit calls for `run`, `confirm`, and `cancel`.
- Pipeline executions now identified by UUID at runtime.
- Removed `durationMs` tracking in favor of external observability (e.g. tracing).
- Organized JDBC access into `PostgresPipelineRepository` and `PostgresStepExecutionRepository`.
- Updated table naming and step identifier from `step_id` to `name`.

### Removed
- No longer auto-confirms steps within `run()` if `autoConfirm = false`.
- Removed internal duration measurements in favor of external metrics.
- Removed table creation responsibility from the starter (delegated to client projects).