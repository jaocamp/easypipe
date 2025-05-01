package com.easypipe.starter.repository.postgres

import com.easypipe.starter.business.Step
import com.easypipe.starter.model.StepExecution
import com.easypipe.starter.repository.StepExecutionRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.Timestamp
import java.sql.Types
import java.util.*

class PostgresStepExecutionRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val objectMapper: ObjectMapper
) : StepExecutionRepository {

    override fun <T> insert(record: StepExecution<T>) {
        val sql = """
            INSERT INTO step_execution (id, pipeline_id, name, status, context_data, created_at)
            VALUES (:id, :pipelineId, :name, :status, cast(:contextData as jsonb), :createdAt)
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("id", record.id)
            .addValue("pipelineId", record.pipelineExecutionId)
            .addValue("name", record.name)
            .addValue("status", record.status.name)
            .addValue("contextData", objectMapper.writeValueAsString(record.contextSnapshot))
            .addValue("createdAt", Timestamp.from(record.createdAt), Types.TIMESTAMP)

        jdbcTemplate.update(sql, params)
    }

    override fun findLastByExecution(pipelineExecutionId: UUID): List<StepExecution<String>> {
        val sql = """
            SELECT DISTINCT ON (name) id, pipeline_id, name, status, context_data::text, created_at
            FROM step_execution
            WHERE pipeline_id = :pipelineId
            ORDER BY name, created_at DESC
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("pipelineId", pipelineExecutionId)

        return jdbcTemplate.query(sql, params, StepExecutionRowMapper())
    }

    private class StepExecutionRowMapper : RowMapper<StepExecution<String>> {
        override fun mapRow(rs: java.sql.ResultSet, rowNum: Int): StepExecution<String> {
            return StepExecution(
                id = UUID.fromString(rs.getString("id")),
                pipelineExecutionId = UUID.fromString(rs.getString("pipeline_id")),
                name = rs.getString("name"),
                status = Step.Status.valueOf(rs.getString("status")),
                contextSnapshot = rs.getString("context_data"),
                createdAt = rs.getTimestamp("created_at").toInstant()
            )
        }
    }
}