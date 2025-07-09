package infrastructure.api.route

import com.example.infrastructure.api.route.invoiceRoutes
import com.example.presentation.dto.InvoiceRegistrationRequest
import com.example.presentation.dto.InvoiceResponse
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

class InvoiceRouteTest {

    private lateinit var database: Database

    @BeforeEach
    fun setup() {
        database = TestDatabaseUtil.createTestDatabase("invoice_route_test")
        TestDatabaseUtil.setupTables(database)
    }

    @AfterEach
    fun cleanup() {
        TestDatabaseUtil.cleanupTables(database)
    }

    @Test
    fun `POST api v1 invoices should create invoice successfully with valid JWT`() = testApplication {
        // Given
        val userId = TestDatabaseUtil.createTestUser(database)
        val token = TestRouteUtil.createValidJwtToken(userId)
        
        TestRouteUtil.configureTestApplication(this, database)

        val request = InvoiceRegistrationRequest(
            paymentAmount = "10000.00",
            paymentDueDate = "2025-12-31"
        )

        // When
        val response = client.post("/api/v1/invoices") {
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(Json.encodeToString(InvoiceRegistrationRequest.serializer(), request))
        }

        // Then
        assertEquals(HttpStatusCode.Created, response.status)
        val result = Json.decodeFromString<InvoiceResponse>(response.bodyAsText())
        assertNotNull(result.id)
        assertEquals(userId, result.userId)
        assertEquals("10000.00", result.paymentAmount)
        assertEquals("2025-12-31", result.paymentDueDate)
    }

    @Test
    fun `POST api v1 invoices should return 401 without JWT token`() = testApplication {
        // Given
        TestRouteUtil.configureTestApplication(this, database)

        val request = InvoiceRegistrationRequest(
            paymentAmount = "10000.00",
            paymentDueDate = "2025-12-31"
        )

        // When
        val response = client.post("/api/v1/invoices") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(Json.encodeToString(InvoiceRegistrationRequest.serializer(), request))
        }

        // Then
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `POST api v1 invoices should return 401 with invalid JWT token`() = testApplication {
        // Given
        TestRouteUtil.configureTestApplication(this, database)

        val request = InvoiceRegistrationRequest(
            paymentAmount = "10000.00",
            paymentDueDate = "2025-12-31"
        )

        // When
        val response = client.post("/api/v1/invoices") {
            header(HttpHeaders.Authorization, "Bearer invalid-token")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(Json.encodeToString(InvoiceRegistrationRequest.serializer(), request))
        }

        // Then
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `GET api v1 invoices should return invoices for authenticated user`() = testApplication {
        // Given
        val userId = TestDatabaseUtil.createTestUser(database)
        val token = TestRouteUtil.createValidJwtToken(userId)
        
        TestRouteUtil.configureTestApplication(this, database)
        
        // Create test invoice first
        TestDatabaseUtil.createTestInvoice(database, userId)

        // When
        val response = client.get("/api/v1/invoices") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        val result = Json.decodeFromString<List<InvoiceResponse>>(response.bodyAsText())
        assertTrue(result.isNotEmpty())
        assertEquals(userId, result.first().userId)
    }

    @Test
    fun `GET api v1 invoices should return 401 without JWT token`() = testApplication {
        // Given
        TestRouteUtil.configureTestApplication(this, database)

        // When
        val response = client.get("/api/v1/invoices")

        // Then
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `GET api v1 invoices should filter by date range when provided`() = testApplication {
        // Given
        val userId = TestDatabaseUtil.createTestUser(database)
        val token = TestRouteUtil.createValidJwtToken(userId)
        
        TestRouteUtil.configureTestApplication(this, database)
        
        // Create test invoices with different payment due dates
        TestDatabaseUtil.createTestInvoice(database, userId, paymentDueDate = "2025-01-15")
        TestDatabaseUtil.createTestInvoice(database, userId, paymentDueDate = "2025-02-15")

        // When
        val response = client.get("/api/v1/invoices") {
            header(HttpHeaders.Authorization, "Bearer $token")
            parameter("startDate", "2025-01-01")
            parameter("endDate", "2025-01-31")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        val result = Json.decodeFromString<List<InvoiceResponse>>(response.bodyAsText())
        assertEquals(1, result.size)
    }
}