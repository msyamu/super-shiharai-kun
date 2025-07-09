package config

object TestAppConfig {
    object Server {
        const val host: String = "localhost"
        const val port: Int = 8080
        const val environment: String = "test"
    }

    object Database {
        const val host: String = "localhost"
        const val port: String = "5432"
        const val name: String = "test_db"
        const val user: String = "test_user"
        const val password: String = "test_password"

        const val jdbcUrl: String = "jdbc:postgresql://$host:$port/$name"
    }

    object Jwt {
        const val secret: String = "test-secret-key-for-testing-purposes-only"
        const val issuer: String = "test-issuer"
        const val expiresInHours: Long = 1L
        const val expiresInMillis: Long = expiresInHours * 60 * 60 * 1000L
        const val expiredOffsetMillis: Long = 1000L // 1 second ago
    }

    object Logging {
        const val level: String = "INFO"
    }
}