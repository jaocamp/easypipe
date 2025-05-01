package com.easypipe.examples.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<Map<String, String>> {
        val response = mapOf("error" to (ex.message ?: "Unexpected error"))
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }
}