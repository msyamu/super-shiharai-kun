package com.example.infrastructure.config

object AppConfig {
    object Server {
        val host: String = getEnvRequired("SERVER_HOST")
        val port: Int = getEnvRequired("SERVER_PORT").toIntOrNull()
            ?: throw IllegalStateException("SERVER_PORT must be a valid integer")
        val environment: String = getEnvRequired("ENVIRONMENT")
    }

    object Database {
        val host: String = getEnvRequired("POSTGRES_HOST")
        val port: String = getEnvRequired("POSTGRES_PORT")
        val name: String = getEnvRequired("POSTGRES_DB")
        val user: String = getEnvRequired("POSTGRES_USER")
        val password: String = getEnvRequired("POSTGRES_PASSWORD")

        val jdbcUrl: String = "jdbc:postgresql://$host:$port/$name"
    }

    object Jwt {
        val secret: String = getEnvRequired("JWT_SECRET").also { secret ->
            require(secret.length >= 32) { 
                "JWT_SECRET must be at least 32 characters (256 bits) for security. Current length: ${secret.length}" 
            }
            require(!secret.matches(Regex("^[a-zA-Z0-9]*$")) || secret.length >= 43) {
                "JWT_SECRET appears to be weak. Use a strong random secret with special characters."
            }
        }
        val issuer: String = getEnvRequired("JWT_ISSUER")
        val expiresInHours: Long = getEnvRequired("JWT_EXPIRES_IN_HOURS").toLongOrNull()
            ?: throw IllegalStateException("JWT_EXPIRES_IN_HOURS must be a valid number")

        val expiresInMillis: Long = expiresInHours * 60 * 60 * 1000L
    }

    object Logging {
        val level: String = getEnvRequired("LOG_LEVEL")
    }

    private fun getEnvRequired(key: String): String {
        return System.getenv(key)
            ?: throw IllegalStateException("$key environment variable is required")
    }
}
