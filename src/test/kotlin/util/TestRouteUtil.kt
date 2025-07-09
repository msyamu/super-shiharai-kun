package util

import com.example.infrastructure.api.route.invoiceRoutes
import com.example.infrastructure.api.route.userRoutes
import com.example.infrastructure.repository.InvoiceRepositoryImpl
import com.example.infrastructure.repository.UserRepositoryImpl
import com.example.application.usecase.InvoiceListUseCase
import com.example.application.usecase.InvoiceRegistrationUseCase
import com.example.application.usecase.LoginUseCase
import com.example.application.usecase.UserRegistrationUseCase
import com.example.presentation.controller.InvoiceController
import com.example.presentation.controller.UserController
import com.example.infrastructure.service.JwtService
import com.example.infrastructure.config.configureErrorHandling
import config.TestAppConfig
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

object TestRouteUtil {
    
    fun configureTestApplication(
        testApplicationEngine: TestApplicationBuilder,
        database: Database
    ) {
        // Set test environment properties for AppConfig
        setTestEnvironmentVariables()
        
        testApplicationEngine.application {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            
            configureErrorHandling()

            install(Authentication) {
                jwt("jwt") {
                    verifier(JWT
                        .require(Algorithm.HMAC256(TestAppConfig.Jwt.secret))
                        .withIssuer(TestAppConfig.Jwt.issuer)
                        .build())
                    validate { credential ->
                        val userId = credential.payload.getClaim("userId").asInt()
                        if (userId != null) {
                            JWTPrincipal(credential.payload)
                        } else null
                    }
                }
            }

            routing {
                setupTestRoutes(database)
            }
        }
    }

    fun createValidJwtToken(userId: Int): String {
        return JWT.create()
            .withIssuer(TestAppConfig.Jwt.issuer)
            .withSubject(userId.toString())
            .withClaim("userId", userId)
            .withExpiresAt(java.util.Date(System.currentTimeMillis() + TestAppConfig.Jwt.expiresInMillis))
            .sign(Algorithm.HMAC256(TestAppConfig.Jwt.secret))
    }

    fun createExpiredJwtToken(userId: Int): String {
        return JWT.create()
            .withIssuer(TestAppConfig.Jwt.issuer)
            .withSubject(userId.toString())
            .withClaim("userId", userId)
            .withExpiresAt(java.util.Date(System.currentTimeMillis() - TestAppConfig.Jwt.expiredOffsetMillis))
            .sign(Algorithm.HMAC256(TestAppConfig.Jwt.secret))
    }

    private fun Route.setupTestRoutes(database: Database) {
        // Use the provided database instance from the test
        // No need to create a new connection
        
        // Initialize repositories
        val userRepository = UserRepositoryImpl()
        val invoiceRepository = InvoiceRepositoryImpl()
        
        // Initialize use cases
        val jwtService = JwtService()
        
        val userRegistrationUseCase = UserRegistrationUseCase(userRepository)
        val loginUseCase = LoginUseCase(userRepository)
        val invoiceRegistrationUseCase = InvoiceRegistrationUseCase(invoiceRepository)
        val invoiceListUseCase = InvoiceListUseCase(invoiceRepository)
        
        // Initialize controllers
        val userController = UserController(userRegistrationUseCase, loginUseCase, jwtService)
        val invoiceController = InvoiceController(invoiceRegistrationUseCase, invoiceListUseCase)
        
        // Setup routes
        userRoutes(userController)
        invoiceRoutes(invoiceController)
    }

    private fun setTestEnvironmentVariables() {
        // Use TestConfigUtil for environment variable setup
        TestConfigUtil.setupTestEnvironment()
    }
}