package com.example.infrastructure.config

import com.example.domain.error.AuthenticationException
import com.example.domain.error.UserAlreadyExistsException
import com.example.presentation.error.InvalidDateFormatException
import com.example.presentation.error.InvalidPageRequestException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.requestvalidation.*
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

        exception<AuthenticationException> { call, cause ->
            logger.warn("Authentication failed: ${cause.message}", cause)
            call.respond(
                HttpStatusCode.Unauthorized,
                mapOf("error" to "Bad request")
            )
        }

        exception<UserAlreadyExistsException> { call, cause ->
            logger.warn("User registration failed: ${cause.message}", cause)
            call.respond(
                HttpStatusCode.Conflict,
                mapOf("error" to "Bad request")
            )
        }

        exception<InvalidDateFormatException> { call, cause ->
            logger.warn("Invalid date format: ${cause.message}", cause)
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Bad request")
            )
        }

        exception<InvalidPageRequestException> { call, cause ->
            logger.warn("Invalid page request: ${cause.message}", cause)
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Bad request")
            )
        }

        exception<RequestValidationException> { call, cause ->
            logger.warn("Request validation failed: ${cause.reasons.joinToString()}", cause)
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
