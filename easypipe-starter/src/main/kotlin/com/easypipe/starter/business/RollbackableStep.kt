package com.easypipe.starter.business

interface RollbackableStep<T> : Step<T> {
    fun rollback(context: T): T
}