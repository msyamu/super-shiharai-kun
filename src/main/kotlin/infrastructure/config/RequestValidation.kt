package com.example.infrastructure.config

import com.example.presentation.dto.BaseRequest
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<BaseRequest> { request ->
            request.validate()
        }
    }
}
