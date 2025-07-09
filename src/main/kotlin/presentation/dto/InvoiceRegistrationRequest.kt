package com.example.presentation.dto

import com.example.infrastructure.serializer.BigDecimalSerializer
import com.example.infrastructure.serializer.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate

@Serializable
data class InvoiceRegistrationRequest(
    @Serializable(with = BigDecimalSerializer::class)
    val paymentAmount: BigDecimal,
    @Serializable(with = LocalDateSerializer::class)
    val paymentDueDate: LocalDate
)
