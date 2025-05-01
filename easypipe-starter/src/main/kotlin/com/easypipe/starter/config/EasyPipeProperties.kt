package com.easypipe.starter.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "easy-pipe")
class EasyPipeProperties(
    val enabled: Boolean = true
)
