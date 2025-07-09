package com.example.infrastructure.config

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun ApplicationCall.getUserIdFromJwt(): Int {
    val principal = principal<JWTPrincipal>()
    return principal?.payload?.subject?.toIntOrNull()
        ?: throw IllegalArgumentException("User ID not found in JWT token")
}
