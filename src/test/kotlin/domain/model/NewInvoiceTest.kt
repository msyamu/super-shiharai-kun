package domain.model

import com.example.domain.model.NewInvoice
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDate

class NewInvoiceTest {

    @Test
    fun `should calculate fee correctly`() {
        // Given
        val paymentAmount = BigDecimal("10000.00")
        val paymentDueDate = LocalDate.of(2025, 12, 31)
        val userId = 1

        // When
        val invoice = NewInvoice(userId, paymentAmount, paymentDueDate)

        // Then
        val expectedFee = BigDecimal("400.00")
        assertEquals(0, expectedFee.compareTo(invoice.fee))
        assertEquals(BigDecimal("0.04"), invoice.feeRate)
    }

    @Test
    fun `should calculate tax amount correctly`() {
        // Given
        val paymentAmount = BigDecimal("10000.00")
        val paymentDueDate = LocalDate.of(2025, 12, 31)
        val userId = 1

        // When
        val invoice = NewInvoice(userId, paymentAmount, paymentDueDate)

        // Then
        val expectedTaxAmount = BigDecimal("40.00")
        assertEquals(0, expectedTaxAmount.compareTo(invoice.taxAmount))
        assertEquals(BigDecimal("0.10"), invoice.taxRate)
    }

    @Test
    fun `should calculate total amount correctly according to business rules`() {
        // Given: 支払金額 10,000円
        val paymentAmount = BigDecimal("10000.00")
        val paymentDueDate = LocalDate.of(2025, 12, 31)
        val userId = 1

        // When
        val invoice = NewInvoice(userId, paymentAmount, paymentDueDate)

        // Then: 10,000 + (10,000 * 0.04 * 1.10) = 10,440円
        val expectedTotal = BigDecimal("10440.00")
        assertEquals(0, expectedTotal.compareTo(invoice.totalAmount))
    }

    @Test
    fun `should set issue date to current date`() {
        // Given
        val paymentAmount = BigDecimal("50000.00")
        val paymentDueDate = LocalDate.of(2025, 12, 31)
        val userId = 1
        val today = LocalDate.now()

        // When
        val invoice = NewInvoice(userId, paymentAmount, paymentDueDate)

        // Then
        assertEquals(today, invoice.issueDate)
    }

    @Test
    fun `should handle different payment amounts correctly`() {
        // Given: 支払金額 50,000円
        val paymentAmount = BigDecimal("50000.00")
        val paymentDueDate = LocalDate.of(2025, 12, 31)
        val userId = 1

        // When
        val invoice = NewInvoice(userId, paymentAmount, paymentDueDate)

        // Then
        val expectedFee = BigDecimal("2000.00")  // 50,000 * 0.04
        val expectedTax = BigDecimal("200.00")   // 2,000 * 0.10
        val expectedTotal = BigDecimal("52200.00") // 50,000 + 2,000 + 200

        assertEquals(0, expectedFee.compareTo(invoice.fee))
        assertEquals(0, expectedTax.compareTo(invoice.taxAmount))
        assertEquals(0, expectedTotal.compareTo(invoice.totalAmount))
    }

    @Test
    fun `should preserve user id and payment due date`() {
        // Given
        val paymentAmount = BigDecimal("25000.00")
        val paymentDueDate = LocalDate.of(2025, 6, 15)
        val userId = 42

        // When
        val invoice = NewInvoice(userId, paymentAmount, paymentDueDate)

        // Then
        assertEquals(userId, invoice.userId)
        assertEquals(paymentDueDate, invoice.paymentDueDate)
        assertEquals(paymentAmount, invoice.paymentAmount)
    }

    @Test
    fun `should handle small amounts with precision`() {
        // Given: 小額での精度テスト
        val paymentAmount = BigDecimal("100.00")
        val paymentDueDate = LocalDate.of(2025, 12, 31)
        val userId = 1

        // When
        val invoice = NewInvoice(userId, paymentAmount, paymentDueDate)

        // Then
        val expectedFee = BigDecimal("4.00")     // 100 * 0.04
        val expectedTax = BigDecimal("0.40")     // 4 * 0.10
        val expectedTotal = BigDecimal("104.40") // 100 + 4 + 0.40

        assertEquals(0, expectedFee.compareTo(invoice.fee))
        assertEquals(0, expectedTax.compareTo(invoice.taxAmount))
        assertEquals(0, expectedTotal.compareTo(invoice.totalAmount))
    }
}