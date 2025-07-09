package com.example.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class NewInvoice(
    val userId: Int,
    val paymentAmount: BigDecimal,
    val paymentDueDate: LocalDate
) {
    companion object {
        private val FEE_RATE = BigDecimal("0.04")
        private val TAX_RATE = BigDecimal("0.10")
    }

    val issueDate: LocalDate = LocalDate.now()
    val fee: BigDecimal = paymentAmount * FEE_RATE
    val feeRate: BigDecimal = FEE_RATE
    val taxAmount: BigDecimal = fee * TAX_RATE
    val taxRate: BigDecimal = TAX_RATE
    val totalAmount: BigDecimal = paymentAmount + fee + taxAmount
}