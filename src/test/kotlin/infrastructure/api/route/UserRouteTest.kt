package infrastructure.api.route

import com.example.infrastructure.api.route.userRoutes
import com.example.presentation.dto.UserRegistrationRequest
import com.example.presentation.dto.LoginRequest
import com.example.presentation.dto.UserResponse
import com.example.presentation.dto.LoginResponse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.jetbrains.exposed.sql.Database
import util.TestDatabaseUtil
import util.TestRouteUtil

class UserRouteTest {

    private lateinit var database: Database

    @BeforeEach
    fun setup() {
        database = TestDatabaseUtil.createTestDatabase("user_route_test")
        TestDatabaseUtil.setupTables(database)
    }

    @AfterEach
    fun cleanup() {
        TestDatabaseUtil.cleanupTables(database)
    }

    @Test
    fun `POST api v1 auth signup should create user successfully`() = testApplication {
        // Given
        TestRouteUtil.configureTestApplication(this, database)

        val request = UserRegistrationRequest(
            companyName = "Test Company",
            name = "Test User",
            email = "test@example.com",
            password = "password123"
        )

        // When
        val response = client.post("/api/v1/auth/signup") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(Json.encodeToString(UserRegistrationRequest.serializer(), request))
        }

        // Then
        assertEquals(HttpStatusCode.Created, response.status)
        val result = Json.decodeFromString<UserResponse>(response.bodyAsText())
        assertNotNull(result.id)
        assertEquals("Test Company", result.companyName)
        assertEquals("Test User", result.name)
        assertEquals("test@example.com", result.email)
    }

    @Test
    fun `POST api v1 auth login should return 401 for mismatched password`() = testApplication {
        // Given
        TestRouteUtil.configureTestApplication(this, database)
        
        // Create test user with properly hashed password for "correctpassword"
        TestDatabaseUtil.createTestUser(
            database,
            email = "login@example.com",
            password = "\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy" // BCrypt hash for "correctpassword"
        )

        val request = LoginRequest(
            email = "login@example.com",
            password = "wrongpassword"
        )

        // When
        val response = client.post("/api/v1/auth/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(Json.encodeToString(LoginRequest.serializer(), request))
        }

        // Then
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `POST api v1 auth login should return 401 for invalid credentials`() = testApplication {
        // Given
        TestRouteUtil.configureTestApplication(this, database)

        val request = LoginRequest(
            email = "nonexistent@example.com",
            password = "wrongpassword"
        )

        // When
        val response = client.post("/api/v1/auth/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(Json.encodeToString(LoginRequest.serializer(), request))
        }

        // Then
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `POST api v1 auth signup should return 400 for duplicate email`() = testApplication {
        // Given
        TestRouteUtil.configureTestApplication(this, database)
        
        // Create user with same email first
        TestDatabaseUtil.createTestUser(
            database,
            email = "duplicate@example.com"
        )

        val request = UserRegistrationRequest(
            companyName = "Another Company",
            name = "Another User",
            email = "duplicate@example.com",
            password = "password123"
        )

        // When
        val response = client.post("/api/v1/auth/signup") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(Json.encodeToString(UserRegistrationRequest.serializer(), request))
        }

        // Then
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST api v1 auth signup should return 400 for invalid request body`() = testApplication {
        // Given
        TestRouteUtil.configureTestApplication(this, database)

        // When - sending invalid JSON
        val response = client.post("/api/v1/auth/signup") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody("{\"invalid\": \"json\"}")
        }

        // Then
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST api v1 auth login should return 400 for invalid request body`() = testApplication {
        // Given
        TestRouteUtil.configureTestApplication(this, database)

        // When - sending invalid JSON
        val response = client.post("/api/v1/auth/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody("{\"invalid\": \"json\"}")
        }

        // Then
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}