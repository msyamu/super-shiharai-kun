package infrastructure.repository

import com.example.domain.model.Invoice
import com.example.domain.model.NewInvoice
import com.example.infrastructure.repository.InvoiceRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import util.TestDatabaseUtil
import java.math.BigDecimal
import java.time.LocalDate

class InvoiceRepositoryImplTest {

    private lateinit var database: Database
    private lateinit var invoiceRepository: InvoiceRepositoryImpl
    private var testUserId1: Int = 0
    private var testUserId2: Int = 0
    private var testUserId42: Int = 0

    @BeforeEach
    fun setup() {
        database = TestDatabaseUtil.createTestDatabase("invoice_repo")
        TestDatabaseUtil.setupTables(database)
        invoiceRepository = InvoiceRepositoryImpl()
        
        // Create test users that will be referenced by invoices
        testUserId1 = TestDatabaseUtil.createTestUser(database, email = "user1@example.com")
        testUserId2 = TestDatabaseUtil.createTestUser(database, email = "user2@example.com")
        testUserId42 = TestDatabaseUtil.createTestUser(database, email = "user42@example.com")
    }

    @AfterEach
    fun cleanup() {
        TestDatabaseUtil.cleanupTables(database)
    }

    @Test
    fun `create should save new invoice and return with generated ID`() = runTest {
        // Given
        val newInvoice = createTestInvoice()

        // When
        val savedInvoice = invoiceRepository.create(newInvoice)

        // Then
        assertNotNull(savedInvoice.id)
        assertTrue(savedInvoice.id > 0)
        assertEquals(newInvoice.userId, savedInvoice.userId)
        assertEquals(newInvoice.issueDate, savedInvoice.issueDate)
        assertEquals(0, newInvoice.paymentAmount.compareTo(savedInvoice.paymentAmount))
        assertEquals(0, newInvoice.fee.compareTo(savedInvoice.fee))
        assertEquals(0, newInvoice.feeRate.compareTo(savedInvoice.feeRate))
        assertEquals(0, newInvoice.taxAmount.compareTo(savedInvoice.taxAmount))
        assertEquals(0, newInvoice.taxRate.compareTo(savedInvoice.taxRate))
        assertEquals(0, newInvoice.totalAmount.compareTo(savedInvoice.totalAmount))
        assertEquals(newInvoice.paymentDueDate, savedInvoice.paymentDueDate)
        assertNotNull(savedInvoice.createdAt)
        assertNotNull(savedInvoice.updatedAt)
    }

    @Test
    fun `findByUserIdWithOptionalDateRange should return all invoices for user when no date range`() = runTest {
        // Given
        val userId = testUserId1
        val invoice1 = createTestInvoice(userId = userId, paymentDueDate = LocalDate.of(2025, 6, 15))
        val invoice2 = createTestInvoice(userId = userId, paymentDueDate = LocalDate.of(2025, 12, 31))
        val invoice3 = createTestInvoice(userId = testUserId2, paymentDueDate = LocalDate.of(2025, 9, 10)) // 別ユーザー

        invoiceRepository.create(invoice1)
        invoiceRepository.create(invoice2)
        invoiceRepository.create(invoice3)

        // When
        val results = invoiceRepository.findByUserIdWithOptionalDateRange(userId, null, null)

        // Then
        assertEquals(2, results.size)
        assertTrue(results.all { it.userId == userId })
    }

    @Test
    fun `findByUserIdWithOptionalDateRange should filter by start date when provided`() = runTest {
        // Given
        val userId = testUserId1
        val invoice1 = createTestInvoice(userId = userId, paymentDueDate = LocalDate.of(2025, 6, 15))
        val invoice2 = createTestInvoice(userId = userId, paymentDueDate = LocalDate.of(2025, 12, 31))
        val invoice3 = createTestInvoice(userId = userId, paymentDueDate = LocalDate.of(2026, 3, 20))

        invoiceRepository.create(invoice1)
        invoiceRepository.create(invoice2)
        invoiceRepository.create(invoice3)

        // When
        val startDate = LocalDate.of(2025, 10, 1)
        val results = invoiceRepository.findByUserIdWithOptionalDateRange(userId, startDate, null)

        // Then
        assertEquals(2, results.size)
        assertTrue(results.all { it.paymentDueDate.isAfter(startDate) || it.paymentDueDate.isEqual(startDate) })
    }

    @Test
    fun `findByUserIdWithOptionalDateRange should filter by end date when provided`() = runTest {
        // Given
        val userId = testUserId1
        val invoice1 = createTestInvoice(userId = userId, paymentDueDate = LocalDate.of(2025, 6, 15))
        val invoice2 = createTestInvoice(userId = userId, paymentDueDate = LocalDate.of(2025, 12, 31))
        val invoice3 = createTestInvoice(userId = userId, paymentDueDate = LocalDate.of(2026, 3, 20))

        invoiceRepository.create(invoice1)
        invoiceRepository.create(invoice2)
        invoiceRepository.create(invoice3)

        // When
        val endDate = LocalDate.of(2025, 12, 31)
        val results = invoiceRepository.findByUserIdWithOptionalDateRange(userId, null, endDate)

        // Then
        assertEquals(2, results.size)
        assertTrue(results.all { it.paymentDueDate.isBefore(endDate) || it.paymentDueDate.isEqual(endDate) })
    }

    @Test
    fun `findByUserIdWithOptionalDateRange should filter by both start and end date when provided`() = runTest {
        // Given
        val userId = testUserId1
        val invoice1 = createTestInvoice(userId = userId, paymentDueDate = LocalDate.of(2025, 6, 15))
        val invoice2 = createTestInvoice(userId = userId, paymentDueDate = LocalDate.of(2025, 12, 31))
        val invoice3 = createTestInvoice(userId = userId, paymentDueDate = LocalDate.of(2026, 3, 20))

        invoiceRepository.create(invoice1)
        invoiceRepository.create(invoice2)
        invoiceRepository.create(invoice3)

        // When
        val startDate = LocalDate.of(2025, 10, 1)
        val endDate = LocalDate.of(2025, 12, 31)
        val results = invoiceRepository.findByUserIdWithOptionalDateRange(userId, startDate, endDate)

        // Then
        assertEquals(1, results.size)
        val result = results[0]
        assertTrue(result.paymentDueDate.isAfter(startDate) || result.paymentDueDate.isEqual(startDate))
        assertTrue(result.paymentDueDate.isBefore(endDate) || result.paymentDueDate.isEqual(endDate))
        assertEquals(LocalDate.of(2025, 12, 31), result.paymentDueDate)
    }

    @Test
    fun `findByUserIdWithOptionalDateRange should return empty list when no invoices match criteria`() = runTest {
        // Given
        val userId = testUserId1
        val invoice1 = createTestInvoice(userId = userId, paymentDueDate = LocalDate.of(2025, 6, 15))
        invoiceRepository.create(invoice1)

        // When
        val startDate = LocalDate.of(2026, 1, 1)
        val endDate = LocalDate.of(2026, 12, 31)
        val results = invoiceRepository.findByUserIdWithOptionalDateRange(userId, startDate, endDate)

        // Then
        assertEquals(0, results.size)
    }

    @Test
    fun `findByUserIdWithOptionalDateRange should return empty list for non-existent user`() = runTest {
        // Given
        val existingUserId = testUserId1
        val nonExistentUserId = 999
        val invoice = createTestInvoice(userId = existingUserId)
        invoiceRepository.create(invoice)

        // When
        val results = invoiceRepository.findByUserIdWithOptionalDateRange(nonExistentUserId, null, null)

        // Then
        assertEquals(0, results.size)
    }

    @Test
    fun `create should handle multiple invoices with same user correctly`() = runTest {
        // Given
        val userId = testUserId1
        val invoice1 = createTestInvoice(
            userId = userId,
            paymentAmount = BigDecimal("10000.00"),
            paymentDueDate = LocalDate.of(2025, 6, 15)
        )
        val invoice2 = createTestInvoice(
            userId = userId,
            paymentAmount = BigDecimal("25000.00"),
            paymentDueDate = LocalDate.of(2025, 12, 31)
        )

        // When
        val savedInvoice1 = invoiceRepository.create(invoice1)
        val savedInvoice2 = invoiceRepository.create(invoice2)

        // Then
        assertNotEquals(savedInvoice1.id, savedInvoice2.id)
        assertEquals(userId, savedInvoice1.userId)
        assertEquals(userId, savedInvoice2.userId)

        val allInvoices = invoiceRepository.findByUserIdWithOptionalDateRange(userId, null, null)
        assertEquals(2, allInvoices.size)
    }

    @Test
    fun `create should preserve all invoice data correctly`() = runTest {
        // Given
        val newInvoice = NewInvoice(
            userId = testUserId42,
            paymentAmount = BigDecimal("123456.78"),
            paymentDueDate = LocalDate.of(2025, 11, 25)
        )

        // When
        val savedInvoice = invoiceRepository.create(newInvoice)

        // Then
        assertEquals(newInvoice.userId, savedInvoice.userId)
        assertEquals(newInvoice.issueDate, savedInvoice.issueDate)
        assertEquals(0, newInvoice.paymentAmount.compareTo(savedInvoice.paymentAmount))
        assertEquals(0, newInvoice.fee.compareTo(savedInvoice.fee))
        assertEquals(0, newInvoice.feeRate.compareTo(savedInvoice.feeRate))
        assertEquals(0, newInvoice.taxAmount.compareTo(savedInvoice.taxAmount))
        assertEquals(0, newInvoice.taxRate.compareTo(savedInvoice.taxRate))
        assertEquals(0, newInvoice.totalAmount.compareTo(savedInvoice.totalAmount))
        assertEquals(newInvoice.paymentDueDate, savedInvoice.paymentDueDate)
    }

    private fun createTestInvoice(
        userId: Int = testUserId1,
        paymentAmount: BigDecimal = BigDecimal("10000.00"),
        paymentDueDate: LocalDate = LocalDate.of(2025, 12, 31)
    ): NewInvoice {
        return NewInvoice(
            userId = userId,
            paymentAmount = paymentAmount,
            paymentDueDate = paymentDueDate
        )
    }
}