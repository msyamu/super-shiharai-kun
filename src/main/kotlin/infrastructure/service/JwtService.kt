package com.example.infrastructure.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.domain.model.User
import com.example.infrastructure.config.AppConfig
import java.util.*

class JwtService {
    private val algorithm = Algorithm.HMAC256(AppConfig.Jwt.secret)
    private val issuer = AppConfig.Jwt.issuer
    private val expiresInMillis = AppConfig.Jwt.expiresInMillis

    private val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(user: User): String {
        return JWT.create()
            .withIssuer(issuer)
            .withSubject(user.id.toString())
            .withClaim("email", user.email)
            .withClaim("companyName", user.companyName)
            .withExpiresAt(Date(System.currentTimeMillis() + expiresInMillis))
            .sign(algorithm)
    }

    fun verifyToken(token: String): DecodedJWT? {
        return try {
            verifier.verify(token)
        } catch (e: Exception) {
            null
        }
    }

    fun getVerifier(): JWTVerifier = verifier
}
