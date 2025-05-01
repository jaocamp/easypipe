package com.easypipe.starter.business

interface Step<T> {
    enum class Status {
        PROCESSED,
        CONFIRMED,
        REVERSED,
        FAILED
    }

    val name: String
    fun run(context: T): T
    fun confirm(context: T): T
}