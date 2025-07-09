package com.example.infrastructure.config

import com.example.infrastructure.service.JwtService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuthentication(jwtService: JwtService) {
    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.getVerifier())

            validate { credential ->
                if (credential.payload.subject != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}
