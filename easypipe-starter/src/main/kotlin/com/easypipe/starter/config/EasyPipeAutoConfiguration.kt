package com.easypipe.starter.config

import com.easypipe.starter.repository.PipelineExecutionRepository
import com.easypipe.starter.repository.StepExecutionRepository
import com.easypipe.starter.repository.postgres.PostgresPipelineExecutionRepository
import com.easypipe.starter.repository.postgres.PostgresStepExecutionRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
@ConditionalOnProperty(prefix = "easy-pipe", name = ["enabled"], havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(EasyPipeProperties::class)
class EasyPipeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(PipelineExecutionRepository::class)
    fun pipelineExecutionRepository(
        jdbcTemplate: NamedParameterJdbcTemplate,
        objectMapper: ObjectMapper
    ): PipelineExecutionRepository {
        return PostgresPipelineExecutionRepository(jdbcTemplate, objectMapper)
    }

    @Bean
    @ConditionalOnMissingBean(StepExecutionRepository::class)
    fun stepExecutionRepository(
        jdbcTemplate: NamedParameterJdbcTemplate,
        objectMapper: ObjectMapper
    ): StepExecutionRepository {
        return PostgresStepExecutionRepository(jdbcTemplate, objectMapper)
    }
}