package infrastructure.api.route

import com.example.infrastructure.api.route.invoiceRoutes
import com.example.presentation.dto.InvoiceRegistrationRequest
import com.example.presentation.dto.InvoiceResponse
import com.example.presentation.dto.PaginatedResponse
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
    fun `GET api v1 invoices should return paginated invoices with default parameters`() = testApplication {
        // Given
        val userId = TestDatabaseUtil.createTestUser(database)
        val token = TestRouteUtil.createValidJwtToken(userId)
        
        TestRouteUtil.configureTestApplication(this, database)
        
        // Create test invoice
        TestDatabaseUtil.createTestInvoice(database, userId)

        // When
        val response = client.get("/api/v1/invoices") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        val result = Json.decodeFromString<PaginatedResponse<InvoiceResponse>>(response.bodyAsText())
        
        assertTrue(result.data.isNotEmpty())
        assertEquals(userId, result.data.first().userId)
        assertEquals(1, result.pagination.page)
        assertEquals(20, result.pagination.limit)
        assertEquals(1, result.pagination.total)
        assertEquals(1, result.pagination.totalPages)
        assertFalse(result.pagination.hasNext)
        assertFalse(result.pagination.hasPrevious)
    }

    @Test
    fun `GET api v1 invoices should return empty paginated response when no invoices exist`() = testApplication {
        // Given
        val userId = TestDatabaseUtil.createTestUser(database)
        val token = TestRouteUtil.createValidJwtToken(userId)
        
        TestRouteUtil.configureTestApplication(this, database)

        // When
        val response = client.get("/api/v1/invoices") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        val result = Json.decodeFromString<PaginatedResponse<InvoiceResponse>>(response.bodyAsText())
        
        assertTrue(result.data.isEmpty())
        assertEquals(1, result.pagination.page)
        assertEquals(20, result.pagination.limit)
        assertEquals(0, result.pagination.total)
        assertEquals(0, result.pagination.totalPages)
        assertFalse(result.pagination.hasNext)
        assertFalse(result.pagination.hasPrevious)
    }

    @Test
    fun `GET api v1 invoices should handle pagination parameters correctly`() = testApplication {
        // Given
        val userId = TestDatabaseUtil.createTestUser(database)
        val token = TestRouteUtil.createValidJwtToken(userId)
        
        TestRouteUtil.configureTestApplication(this, database)
        
        // Create multiple test invoices
        repeat(5) {
            TestDatabaseUtil.createTestInvoice(database, userId)
        }

        // When
        val response = client.get("/api/v1/invoices") {
            header(HttpHeaders.Authorization, "Bearer $token")
            parameter("page", "2")
            parameter("size", "2")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        val result = Json.decodeFromString<PaginatedResponse<InvoiceResponse>>(response.bodyAsText())
        
        assertEquals(2, result.data.size)
        assertEquals(2, result.pagination.page)
        assertEquals(2, result.pagination.limit)
        assertEquals(5, result.pagination.total)
        assertEquals(3, result.pagination.totalPages)
        assertTrue(result.pagination.hasNext)
        assertTrue(result.pagination.hasPrevious)
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
        val result = Json.decodeFromString<PaginatedResponse<InvoiceResponse>>(response.bodyAsText())
        assertEquals(1, result.data.size)
        assertEquals("2025-01-15", result.data.first().paymentDueDate)
    }

    @Test
    fun `GET api v1 invoices should return 400 when startDate is after endDate`() = testApplication {
        // Given
        val userId = TestDatabaseUtil.createTestUser(database)
        val token = TestRouteUtil.createValidJwtToken(userId)
        
        TestRouteUtil.configureTestApplication(this, database)

        // When
        val response = client.get("/api/v1/invoices") {
            header(HttpHeaders.Authorization, "Bearer $token")
            parameter("startDate", "2025-02-01")
            parameter("endDate", "2025-01-31")
        }

        // Then
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `GET api v1 invoices should handle invalid page parameters gracefully`() = testApplication {
        // Given
        val userId = TestDatabaseUtil.createTestUser(database)
        val token = TestRouteUtil.createValidJwtToken(userId)
        
        TestRouteUtil.configureTestApplication(this, database)

        // When - invalid page (less than 1)
        val response = client.get("/api/v1/invoices") {
            header(HttpHeaders.Authorization, "Bearer $token")
            parameter("page", "0")
        }

        // Then - should use default page 1
        assertEquals(HttpStatusCode.OK, response.status)
        val result = Json.decodeFromString<PaginatedResponse<InvoiceResponse>>(response.bodyAsText())
        assertEquals(1, result.pagination.page)
    }

    @Test
    fun `GET api v1 invoices should only return invoices for authenticated user`() = testApplication {
        // Given
        val userId1 = TestDatabaseUtil.createTestUser(database)
        val userId2 = TestDatabaseUtil.createTestUser(database, email = "user2@example.com")
        val token1 = TestRouteUtil.createValidJwtToken(userId1)
        
        TestRouteUtil.configureTestApplication(this, database)
        
        // Create invoices for both users
        TestDatabaseUtil.createTestInvoice(database, userId1)
        TestDatabaseUtil.createTestInvoice(database, userId2)

        // When
        val response = client.get("/api/v1/invoices") {
            header(HttpHeaders.Authorization, "Bearer $token1")
        }

        // Then
        assertEquals(HttpStatusCode.OK, response.status)
        val result = Json.decodeFromString<PaginatedResponse<InvoiceResponse>>(response.bodyAsText())
        
        assertEquals(1, result.data.size)
        assertEquals(userId1, result.data.first().userId)
    }
}