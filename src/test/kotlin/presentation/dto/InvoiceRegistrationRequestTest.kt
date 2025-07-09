package presentation.dto

import com.example.presentation.dto.InvoiceRegistrationRequest
import io.ktor.server.plugins.requestvalidation.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDate

class InvoiceRegistrationRequestTest {

    @Test
    fun `should return Valid when all fields are valid`() {
        // Given
        val tomorrow = LocalDate.now().plusDays(1)
        val request = InvoiceRegistrationRequest(
            paymentAmount = BigDecimal("1000.00"),
            paymentDueDate = tomorrow
        )

        // When
        val result = request.validate()

        // Then
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `should return Invalid when paymentAmount is zero`() {
        // Given
        val tomorrow = LocalDate.now().plusDays(1)
        val request = InvoiceRegistrationRequest(
            paymentAmount = BigDecimal.ZERO,
            paymentDueDate = tomorrow
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Payment amount must be positive", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Invalid when paymentAmount is negative`() {
        // Given
        val tomorrow = LocalDate.now().plusDays(1)
        val request = InvoiceRegistrationRequest(
            paymentAmount = BigDecimal("-100.00"),
            paymentDueDate = tomorrow
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Payment amount must be positive", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Invalid when paymentAmount exceeds maximum`() {
        // Given
        val tomorrow = LocalDate.now().plusDays(1)
        val tooLargeAmount = BigDecimal("10000000000000.00")
        val request = InvoiceRegistrationRequest(
            paymentAmount = tooLargeAmount,
            paymentDueDate = tomorrow
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Payment amount cannot exceed 9999999999999.99", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Invalid when paymentDueDate is in the past`() {
        // Given
        val yesterday = LocalDate.now().minusDays(1)
        val request = InvoiceRegistrationRequest(
            paymentAmount = BigDecimal("1000.00"),
            paymentDueDate = yesterday
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Payment due date cannot be in the past", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Invalid when paymentDueDate is too far in the future`() {
        // Given
        val tooFarFuture = LocalDate.now().plusYears(1).plusDays(1)
        val request = InvoiceRegistrationRequest(
            paymentAmount = BigDecimal("1000.00"),
            paymentDueDate = tooFarFuture
        )

        // When
        val result = request.validate()

        // Then
        assertTrue(result is ValidationResult.Invalid)
        assertEquals("Payment due date cannot be more than 1 year from today", (result as ValidationResult.Invalid).reasons.first())
    }

    @Test
    fun `should return Valid when paymentAmount is at maximum allowed value`() {
        // Given
        val tomorrow = LocalDate.now().plusDays(1)
        val request = InvoiceRegistrationRequest(
            paymentAmount = BigDecimal("9999999999999.99"),
            paymentDueDate = tomorrow
        )

        // When
        val result = request.validate()

        // Then
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `should return Valid when paymentDueDate is exactly one year from today`() {
        // Given
        val oneYearFromToday = LocalDate.now().plusYears(1)
        val request = InvoiceRegistrationRequest(
            paymentAmount = BigDecimal("1000.00"),
            paymentDueDate = oneYearFromToday
        )

        // When
        val result = request.validate()

        // Then
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `should return Valid when paymentDueDate is today`() {
        // Given
        val today = LocalDate.now()
        val request = InvoiceRegistrationRequest(
            paymentAmount = BigDecimal("1000.00"),
            paymentDueDate = today
        )

        // When
        val result = request.validate()

        // Then
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun `should return Valid with minimum positive amount`() {
        // Given
        val tomorrow = LocalDate.now().plusDays(1)
        val request = InvoiceRegistrationRequest(
            paymentAmount = BigDecimal("0.01"),
            paymentDueDate = tomorrow
        )

        // When
        val result = request.validate()

        // Then
        assertEquals(ValidationResult.Valid, result)
    }
}