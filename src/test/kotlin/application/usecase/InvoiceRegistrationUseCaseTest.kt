package application.usecase

import com.example.application.usecase.InvoiceRegistrationUseCase
import com.example.domain.model.Invoice
import com.example.domain.model.NewInvoice
import com.example.domain.repository.InvoiceRepository
import com.example.presentation.dto.InvoiceRegistrationRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class InvoiceRegistrationUseCaseTest {

    private val invoiceRepository = mockk<InvoiceRepository>()
    private val invoiceRegistrationUseCase = InvoiceRegistrationUseCase(invoiceRepository)

    @Test
    fun `should register invoice successfully`() = runTest {
        // Given
        val userId = 1
        val request = InvoiceRegistrationRequest(
            paymentAmount = BigDecimal("10000.00"),
            paymentDueDate = LocalDate.of(2025, 12, 31)
        )

        val savedInvoice = Invoice(
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

        coEvery { invoiceRepository.create(any<NewInvoice>()) } returns savedInvoice

        // When
        val result = invoiceRegistrationUseCase.execute(userId, request)

        // Then
        assertEquals(savedInvoice, result)
        coVerify { 
            invoiceRepository.create(match<NewInvoice> { newInvoice ->
                newInvoice.userId == userId &&
                newInvoice.paymentAmount.compareTo(BigDecimal("10000.00")) == 0 &&
                newInvoice.paymentDueDate == LocalDate.of(2025, 12, 31)
            })
        }
    }

}