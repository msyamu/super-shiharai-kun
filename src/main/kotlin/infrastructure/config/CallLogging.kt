package com.example.infrastructure.config

import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

fun Application.configureCallLogging() {
    install(CallLogging) {
        level = Level.INFO
        logger = LoggerFactory.getLogger("Access")
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.local.method.value
            val uri = call.request.local.uri
            val userAgent = call.request.headers["User-Agent"]
            "$status: $httpMethod $uri - $userAgent"
        }
    }
}
