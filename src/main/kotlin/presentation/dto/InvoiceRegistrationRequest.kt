package com.example.presentation.dto

import com.example.infrastructure.serializer.BigDecimalSerializer
import com.example.infrastructure.serializer.LocalDateSerializer
import io.ktor.server.plugins.requestvalidation.*
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate

@Serializable
data class InvoiceRegistrationRequest(
    @Serializable(with = BigDecimalSerializer::class)
    val paymentAmount: BigDecimal,
    @Serializable(with = LocalDateSerializer::class)
    val paymentDueDate: LocalDate
) : BaseRequest {
    
    companion object {
        private val MAX_PAYMENT_AMOUNT = BigDecimal("9999999999999.99")
        private const val MAX_PAYMENT_DUE_DATE_YEARS = 1L
    }
    
    override fun validate(): ValidationResult {
        val today = LocalDate.now()
        return when {
            paymentAmount <= BigDecimal.ZERO -> 
                ValidationResult.Invalid("Payment amount must be positive")
            paymentAmount > MAX_PAYMENT_AMOUNT -> 
                ValidationResult.Invalid("Payment amount cannot exceed $MAX_PAYMENT_AMOUNT")
            paymentDueDate.isBefore(today) -> 
                ValidationResult.Invalid("Payment due date cannot be in the past")
            paymentDueDate.isAfter(today.plusYears(MAX_PAYMENT_DUE_DATE_YEARS)) -> 
                ValidationResult.Invalid("Payment due date cannot be more than $MAX_PAYMENT_DUE_DATE_YEARS year from today")
            else -> ValidationResult.Valid
        }
    }
}
