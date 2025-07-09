package com.example.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class Invoice(
    val id: Int,
    val userId: Int,
    val issueDate: LocalDate,
    val paymentAmount: BigDecimal,
    val fee: BigDecimal,
    val feeRate: BigDecimal,
    val taxAmount: BigDecimal,
    val taxRate: BigDecimal,
    val totalAmount: BigDecimal,
    val paymentDueDate: LocalDate,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)