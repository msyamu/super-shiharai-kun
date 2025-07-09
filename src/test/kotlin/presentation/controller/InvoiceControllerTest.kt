package presentation.controller

import com.example.application.usecase.InvoiceListUseCase
import com.example.application.usecase.InvoiceRegistrationUseCase
import com.example.domain.model.Invoice
import com.example.domain.model.Page
import com.example.domain.model.PageRequest
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
            paymentAmount = BigDecimal("10000.00"),
            paymentDueDate = LocalDate.of(2025, 12, 31)
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
            paymentAmount = BigDecimal("10000.00"),
            paymentDueDate = LocalDate.of(2025, 12, 31)
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

        val pageRequest = PageRequest(page = 1, size = 20)
        val page = Page(content = invoices, totalElements = invoices.size.toLong(), pageRequest = pageRequest)
        coEvery { invoiceListUseCase.execute(userId, null, null, pageRequest) } returns page

        // When
        val result = invoiceController.getInvoices(userId, null, null, 1, 20)

        // Then
        assertEquals(2, result.data.size)
        assertEquals(invoices[0].id, result.data[0].id)
        assertEquals(invoices[1].id, result.data[1].id)
        assertEquals(userId, result.data[0].userId)
        assertEquals(userId, result.data[1].userId)
        
        coVerify { invoiceListUseCase.execute(userId, null, null, pageRequest) }
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

        val pageRequest = PageRequest(page = 1, size = 20)
        val page = Page(content = filteredInvoices, totalElements = filteredInvoices.size.toLong(), pageRequest = pageRequest)
        coEvery { invoiceListUseCase.execute(userId, startDate, endDate, pageRequest) } returns page

        // When
        val result = invoiceController.getInvoices(userId, startDate?.toString(), endDate?.toString(), 1, 20)

        // Then
        assertEquals(1, result.data.size)
        assertEquals(filteredInvoices[0].id, result.data[0].id)
        assertEquals(userId, result.data[0].userId)
        
        coVerify { invoiceListUseCase.execute(userId, startDate, endDate, pageRequest) }
    }

    @Test
    fun `listInvoices should return empty list when no invoices found`() = runTest {
        // Given
        val userId = 1
        val emptyList = emptyList<Invoice>()

        val pageRequest = PageRequest(page = 1, size = 20)
        val page = Page(content = emptyList, totalElements = 0L, pageRequest = pageRequest)
        coEvery { invoiceListUseCase.execute(userId, null, null, pageRequest) } returns page

        // When
        val result = invoiceController.getInvoices(userId, null, null, 1, 20)

        // Then
        assertEquals(0, result.data.size)
        
        coVerify { invoiceListUseCase.execute(userId, null, null, pageRequest) }
    }

    @Test
    fun `listInvoices should propagate exception when usecase throws exception`() = runTest {
        // Given
        val userId = 999 // 存在しないユーザー
        
        val pageRequest = PageRequest(page = 1, size = 20)
        coEvery { invoiceListUseCase.execute(userId, null, null, pageRequest) } throws IllegalArgumentException("User not found")

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            invoiceController.getInvoices(userId, null, null, 1, 20)
        }
        assertEquals("User not found", exception.message)
        
        coVerify { invoiceListUseCase.execute(userId, null, null, pageRequest) }
    }

    @Test
    fun `registerInvoice should handle different payment amounts correctly`() = runTest {
        // Given
        val userId = 2
        val request = InvoiceRegistrationRequest(
            paymentAmount = BigDecimal("10000.00"),
            paymentDueDate = LocalDate.of(2025, 12, 31)
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

        val pageRequest = PageRequest(page = 1, size = 20)
        val page = Page(content = filteredInvoices, totalElements = filteredInvoices.size.toLong(), pageRequest = pageRequest)
        coEvery { invoiceListUseCase.execute(userId, startDate, null, pageRequest) } returns page

        // When
        val result = invoiceController.getInvoices(userId, startDate?.toString(), null, 1, 20)

        // Then
        assertEquals(1, result.data.size)
        assertEquals(filteredInvoices[0].id, result.data[0].id)
        
        coVerify { invoiceListUseCase.execute(userId, startDate, null, pageRequest) }
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

        val pageRequest = PageRequest(page = 1, size = 20)
        val page = Page(content = filteredInvoices, totalElements = filteredInvoices.size.toLong(), pageRequest = pageRequest)
        coEvery { invoiceListUseCase.execute(userId, null, endDate, pageRequest) } returns page

        // When
        val result = invoiceController.getInvoices(userId, null, endDate?.toString(), 1, 20)

        // Then
        assertEquals(1, result.data.size)
        assertEquals(filteredInvoices[0].id, result.data[0].id)
        
        coVerify { invoiceListUseCase.execute(userId, null, endDate, pageRequest) }
    }

    @Test
    fun `getInvoices should throw exception when page number is less than 1`() = runTest {
        // Given
        val userId = 1

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            invoiceController.getInvoices(userId, null, null, 0, 20)
        }
        assertEquals("Page number must be at least 1", exception.message)
    }

    @Test
    fun `getInvoices should throw exception when page size is less than 1`() = runTest {
        // Given
        val userId = 1

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            invoiceController.getInvoices(userId, null, null, 1, 0)
        }
        assertEquals("Page size must be between 1 and 100", exception.message)
    }

    @Test
    fun `getInvoices should throw exception when page size exceeds maximum`() = runTest {
        // Given
        val userId = 1

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            invoiceController.getInvoices(userId, null, null, 1, 101)
        }
        assertEquals("Page size must be between 1 and 100", exception.message)
    }

    @Test
    fun `getInvoices should throw exception when start date is blank`() = runTest {
        // Given
        val userId = 1

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            invoiceController.getInvoices(userId, "", null, 1, 20)
        }
        assertEquals("Start date cannot be blank", exception.message)
    }

    @Test
    fun `getInvoices should throw exception when end date is blank`() = runTest {
        // Given
        val userId = 1

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            invoiceController.getInvoices(userId, null, "", 1, 20)
        }
        assertEquals("End date cannot be blank", exception.message)
    }

    @Test
    fun `getInvoices should throw exception when start date format is invalid`() = runTest {
        // Given
        val userId = 1

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            invoiceController.getInvoices(userId, "2025-13-01", null, 1, 20)
        }
        assertEquals("Invalid start date format", exception.message)
    }

    @Test
    fun `getInvoices should throw exception when end date format is invalid`() = runTest {
        // Given
        val userId = 1

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            invoiceController.getInvoices(userId, null, "invalid-date", 1, 20)
        }
        assertEquals("Invalid end date format", exception.message)
    }
}