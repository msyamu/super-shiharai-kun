package presentation.controller

import com.example.application.usecase.InvoiceListUseCase
import com.example.application.usecase.InvoiceRegistrationUseCase
import com.example.domain.model.Invoice
import com.example.presentation.controller.InvoiceController
import com.example.presentation.dto.InvoiceRegistrationRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class InvoiceControllerTest {

    private lateinit var invoiceRegistrationUseCase: InvoiceRegistrationUseCase
    private lateinit var invoiceListUseCase: InvoiceListUseCase
    private lateinit var invoiceController: InvoiceController

    @BeforeEach
    fun setup() {
        invoiceRegistrationUseCase = mockk()
        invoiceListUseCase = mockk()
        invoiceController = InvoiceController(invoiceRegistrationUseCase, invoiceListUseCase)
    }

    @Test
    fun `registerInvoice should return InvoiceResponse when registration is successful`() = runTest {
        // Given
        val userId = 1
        val request = InvoiceRegistrationRequest(
            paymentAmount = "10000.00",
            paymentDueDate = "2025-12-31"
        )
        
        val registeredInvoice = Invoice(
            id = 1,
            userId = userId,
            issueDate = LocalDate.now(),
            paymentAmount = BigDecimal("10000.00"),
            fee = BigDecimal("400.00"),
            feeRate = BigDecimal("0.04"),
            taxAmount = BigDecimal("40.00"),
            taxRate = BigDecimal("0.10"),
            totalAmount = BigDecimal("10440.00"),
            paymentDueDate = LocalDate.of(2025, 12, 31),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        coEvery { invoiceRegistrationUseCase.execute(userId, request) } returns registeredInvoice

        // When
        val result = invoiceController.registerInvoice(userId, request)

        // Then
        assertEquals(registeredInvoice.id, result.id)
        assertEquals(registeredInvoice.userId, result.userId)
        assertEquals(registeredInvoice.issueDate.toString(), result.issueDate)
        assertEquals(registeredInvoice.paymentAmount.toString(), result.paymentAmount)
        assertEquals(registeredInvoice.fee.toString(), result.fee)
        assertEquals(registeredInvoice.feeRate.toString(), result.feeRate)
        assertEquals(registeredInvoice.taxAmount.toString(), result.taxAmount)
        assertEquals(registeredInvoice.taxRate.toString(), result.taxRate)
        assertEquals(registeredInvoice.totalAmount.toString(), result.totalAmount)
        assertEquals(registeredInvoice.paymentDueDate.toString(), result.paymentDueDate)
        
        coVerify { invoiceRegistrationUseCase.execute(userId, request) }
    }

    @Test
    fun `registerInvoice should propagate exception when usecase throws exception`() = runTest {
        // Given
        val userId = 1
        val request = InvoiceRegistrationRequest(
            paymentAmount = "invalid-amount",
            paymentDueDate = "2025-12-31"
        )

        coEvery { invoiceRegistrationUseCase.execute(userId, request) } throws IllegalArgumentException("Invalid payment amount")

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            invoiceController.registerInvoice(userId, request)
        }
        assertEquals("Invalid payment amount", exception.message)
        
        coVerify { invoiceRegistrationUseCase.execute(userId, request) }
    }

    @Test
    fun `listInvoices should return list of InvoiceResponse when no date filters`() = runTest {
        // Given
        val userId = 1
        val invoices = listOf(
            Invoice(
                id = 1,
                userId = userId,
                issueDate = LocalDate.of(2025, 7, 9),
                paymentAmount = BigDecimal("10000.00"),
                fee = BigDecimal("400.00"),
                feeRate = BigDecimal("0.04"),
                taxAmount = BigDecimal("40.00"),
                taxRate = BigDecimal("0.10"),
                totalAmount = BigDecimal("10440.00"),
                paymentDueDate = LocalDate.of(2025, 12, 31),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Invoice(
                id = 2,
                userId = userId,
                issueDate = LocalDate.of(2025, 7, 9),
                paymentAmount = BigDecimal("5000.00"),
                fee = BigDecimal("200.00"),
                feeRate = BigDecimal("0.04"),
                taxAmount = BigDecimal("20.00"),
                taxRate = BigDecimal("0.10"),
                totalAmount = BigDecimal("5220.00"),
                paymentDueDate = LocalDate.of(2025, 11, 30),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        coEvery { invoiceListUseCase.execute(userId, null, null) } returns invoices

        // When
        val result = invoiceController.getInvoices(userId, null, null)

        // Then
        assertEquals(2, result.size)
        assertEquals(invoices[0].id, result[0].id)
        assertEquals(invoices[1].id, result[1].id)
        assertEquals(userId, result[0].userId)
        assertEquals(userId, result[1].userId)
        
        coVerify { invoiceListUseCase.execute(userId, null, null) }
    }

    @Test
    fun `listInvoices should return filtered list when date filters are provided`() = runTest {
        // Given
        val userId = 1
        val startDate = LocalDate.of(2025, 1, 1)
        val endDate = LocalDate.of(2025, 12, 31)
        val filteredInvoices = listOf(
            Invoice(
                id = 1,
                userId = userId,
                issueDate = LocalDate.of(2025, 7, 9),
                paymentAmount = BigDecimal("10000.00"),
                fee = BigDecimal("400.00"),
                feeRate = BigDecimal("0.04"),
                taxAmount = BigDecimal("40.00"),
                taxRate = BigDecimal("0.10"),
                totalAmount = BigDecimal("10440.00"),
                paymentDueDate = LocalDate.of(2025, 12, 31),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        coEvery { invoiceListUseCase.execute(userId, startDate, endDate) } returns filteredInvoices

        // When
        val result = invoiceController.getInvoices(userId, startDate?.toString(), endDate?.toString())

        // Then
        assertEquals(1, result.size)
        assertEquals(filteredInvoices[0].id, result[0].id)
        assertEquals(userId, result[0].userId)
        
        coVerify { invoiceListUseCase.execute(userId, startDate, endDate) }
    }

    @Test
    fun `listInvoices should return empty list when no invoices found`() = runTest {
        // Given
        val userId = 1
        val emptyList = emptyList<Invoice>()

        coEvery { invoiceListUseCase.execute(userId, null, null) } returns emptyList

        // When
        val result = invoiceController.getInvoices(userId, null, null)

        // Then
        assertEquals(0, result.size)
        
        coVerify { invoiceListUseCase.execute(userId, null, null) }
    }

    @Test
    fun `listInvoices should propagate exception when usecase throws exception`() = runTest {
        // Given
        val userId = 999 // 存在しないユーザー
        
        coEvery { invoiceListUseCase.execute(userId, null, null) } throws IllegalArgumentException("User not found")

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            invoiceController.getInvoices(userId, null, null)
        }
        assertEquals("User not found", exception.message)
        
        coVerify { invoiceListUseCase.execute(userId, null, null) }
    }

    @Test
    fun `registerInvoice should handle different payment amounts correctly`() = runTest {
        // Given
        val userId = 2
        val request = InvoiceRegistrationRequest(
            paymentAmount = "50000.00",
            paymentDueDate = "2025-06-15"
        )
        
        val registeredInvoice = Invoice(
            id = 2,
            userId = userId,
            issueDate = LocalDate.now(),
            paymentAmount = BigDecimal("50000.00"),
            fee = BigDecimal("2000.00"),
            feeRate = BigDecimal("0.04"),
            taxAmount = BigDecimal("200.00"),
            taxRate = BigDecimal("0.10"),
            totalAmount = BigDecimal("52200.00"),
            paymentDueDate = LocalDate.of(2025, 6, 15),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        coEvery { invoiceRegistrationUseCase.execute(userId, request) } returns registeredInvoice

        // When
        val result = invoiceController.registerInvoice(userId, request)

        // Then
        assertEquals("50000.00", result.paymentAmount)
        assertEquals("2000.00", result.fee)
        assertEquals("200.00", result.taxAmount)
        assertEquals("52200.00", result.totalAmount)
        assertEquals("2025-06-15", result.paymentDueDate)
        
        coVerify { invoiceRegistrationUseCase.execute(userId, request) }
    }

    @Test
    fun `listInvoices should handle only start date filter`() = runTest {
        // Given
        val userId = 1
        val startDate = LocalDate.of(2025, 6, 1)
        val filteredInvoices = listOf(
            Invoice(
                id = 1,
                userId = userId,
                issueDate = LocalDate.of(2025, 7, 9),
                paymentAmount = BigDecimal("10000.00"),
                fee = BigDecimal("400.00"),
                feeRate = BigDecimal("0.04"),
                taxAmount = BigDecimal("40.00"),
                taxRate = BigDecimal("0.10"),
                totalAmount = BigDecimal("10440.00"),
                paymentDueDate = LocalDate.of(2025, 12, 31),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        coEvery { invoiceListUseCase.execute(userId, startDate, null) } returns filteredInvoices

        // When
        val result = invoiceController.getInvoices(userId, startDate?.toString(), null)

        // Then
        assertEquals(1, result.size)
        assertEquals(filteredInvoices[0].id, result[0].id)
        
        coVerify { invoiceListUseCase.execute(userId, startDate, null) }
    }

    @Test
    fun `listInvoices should handle only end date filter`() = runTest {
        // Given
        val userId = 1
        val endDate = LocalDate.of(2025, 12, 31)
        val filteredInvoices = listOf(
            Invoice(
                id = 1,
                userId = userId,
                issueDate = LocalDate.of(2025, 7, 9),
                paymentAmount = BigDecimal("10000.00"),
                fee = BigDecimal("400.00"),
                feeRate = BigDecimal("0.04"),
                taxAmount = BigDecimal("40.00"),
                taxRate = BigDecimal("0.10"),
                totalAmount = BigDecimal("10440.00"),
                paymentDueDate = LocalDate.of(2025, 12, 31),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        coEvery { invoiceListUseCase.execute(userId, null, endDate) } returns filteredInvoices

        // When
        val result = invoiceController.getInvoices(userId, null, endDate?.toString())

        // Then
        assertEquals(1, result.size)
        assertEquals(filteredInvoices[0].id, result[0].id)
        
        coVerify { invoiceListUseCase.execute(userId, null, endDate) }
    }
}