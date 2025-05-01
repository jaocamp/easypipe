package com.easypipe.starter.repository.postgres

import com.easypipe.starter.business.Pipeline
import com.easypipe.starter.repository.PipelineExecutionRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.Timestamp
import java.sql.Types
import java.time.Instant
import java.util.*

class PostgresPipelineExecutionRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper
) : PipelineExecutionRepository {

    override fun <T> insert(id: UUID, pipelineName: String, status: Pipeline.Status, contextSnapshot: T, createdAt: Instant) {
        val sql = """
            INSERT INTO pipeline_execution (id, name, status, context_data, created_at)
            VALUES (:id, :name, :status, cast(:contextData as jsonb), :createdAt)
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("id", id)
            .addValue("name", pipelineName)
            .addValue("status", status.name)
            .addValue("contextData", objectMapper.writeValueAsString(contextSnapshot))
            .addValue("createdAt", Timestamp.from(createdAt), Types.TIMESTAMP)

        jdbcTemplate.update(sql, params)
    }

    override fun updateStatus(id: UUID, status: Pipeline.Status) {
        val sql = """
            UPDATE pipeline_execution
            SET status = :status
            WHERE id = :id
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("id", id)
            .addValue("status", status.name)

        jdbcTemplate.update(sql, params)
    }
}