package com.example.presentation.route

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: String
)

fun Route.healthRoutes() {
    get("/health") {
        val response = HealthResponse(
            status = "OK",
            timestamp = java.time.Instant.now().toString()
        )
        call.respond(HttpStatusCode.OK, response)
    }
}