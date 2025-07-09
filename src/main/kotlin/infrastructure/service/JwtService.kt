package com.example.infrastructure.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.domain.model.User
import com.example.infrastructure.config.AppConfig
import java.util.*

class JwtService {
    private val algorithm = Algorithm.HMAC256(AppConfig.Jwt.secret)
    private val issuer = AppConfig.Jwt.issuer
    private val expiresInMillis = AppConfig.Jwt.expiresInMillis

    fun generateToken(user: User): String {
        return JWT.create()
            .withIssuer(issuer)
            .withSubject(user.id.toString())
            .withClaim("email", user.email)
            .withClaim("companyName", user.companyName)
            .withExpiresAt(Date(System.currentTimeMillis() + expiresInMillis))
            .sign(algorithm)
    }
}