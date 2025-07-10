package com.example.infrastructure.config

import com.example.domain.error.AuthenticationException
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun ApplicationCall.getUserIdFromJwt(): Int {
    val principal = principal<JWTPrincipal>()
    return principal?.payload?.subject?.toIntOrNull()
        ?: throw AuthenticationException("User ID not found in JWT token")
}
