package infrastructure.api.route

import com.example.infrastructure.api.route.healthRoutes
import com.example.infrastructure.api.route.HealthResponse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
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

class HealthRouteTest {

    private lateinit var database: Database

    @BeforeEach
    fun setup() {
        database = TestDatabaseUtil.createTestDatabase("health_route_test")
        TestDatabaseUtil.setupTables(database)
    }

    @AfterEach
    fun cleanup() {
        TestDatabaseUtil.cleanupTables(database)
    }

    @Test
    fun `GET health should return 200 with status OK`() = testApplication {
        // Given
        TestRouteUtil.configureTestApplication(this, database)
        
        application {
            routing {
                healthRoutes()
            }
        }

        // When
        val response = client.get("/health")

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        val result = Json.decodeFromString<HealthResponse>(response.bodyAsText())
        assertEquals("OK", result.status)
        assertNotNull(result.timestamp)
    }
}
