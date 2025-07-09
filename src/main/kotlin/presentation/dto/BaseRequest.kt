package com.example.presentation.dto

import io.ktor.server.plugins.requestvalidation.*

interface BaseRequest {
    fun validate(): ValidationResult
}