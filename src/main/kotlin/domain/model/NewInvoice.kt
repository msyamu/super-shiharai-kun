package com.example.domain.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

data class NewInvoice(
    val userId: Int,
    val paymentAmount: BigDecimal,
    val paymentDueDate: LocalDate
) {
    companion object {
        val FEE_RATE = BigDecimal("0.04")
        val TAX_RATE = BigDecimal("0.10")
    }
    
    val issueDate: LocalDate = LocalDate.now()
    val fee: BigDecimal = (paymentAmount * FEE_RATE).setScale(2, RoundingMode.HALF_UP)
    val feeRate: BigDecimal = FEE_RATE
    val taxAmount: BigDecimal = (fee * TAX_RATE).setScale(2, RoundingMode.HALF_UP)
    val taxRate: BigDecimal = TAX_RATE
    val totalAmount: BigDecimal = (paymentAmount + fee + taxAmount).setScale(2, RoundingMode.HALF_UP)
}
