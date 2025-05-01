package com.easypipe.examples.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("EasyPipe Example API")
                    .version("1.0")
                    .description("REST endpoints for executing pipelines with EasyPipe")
            )
    }
}