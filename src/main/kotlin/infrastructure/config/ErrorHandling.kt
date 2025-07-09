package com.example.infrastructure.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.slf4j.LoggerFactory

fun Application.configureErrorHandling() {
    val logger = LoggerFactory.getLogger("Application")

    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            logger.warn("Bad request: ${cause.message}", cause)
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Bad request")
            )
        }

        exception<IllegalArgumentException> { call, cause ->
            logger.warn("Bad request: ${cause.message}", cause)
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Bad request")
            )
        }

        exception<IllegalStateException> { call, cause ->
            logger.error("Internal server error: ${cause.message}", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Internal server error")
            )
        }

        exception<Exception> { call, cause ->
            if (cause is BadRequestException) return@exception
            logger.error("Unexpected error: ${cause.message}", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Internal server error")
            )
        }
    }
}
