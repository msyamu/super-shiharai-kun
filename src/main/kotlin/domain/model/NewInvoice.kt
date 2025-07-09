package com.example.domain.model

import com.example.infrastructure.config.Constants
import java.math.BigDecimal
import java.time.LocalDate

data class NewInvoice(
    val userId: Int,
    val paymentAmount: BigDecimal,
    val paymentDueDate: LocalDate
) {
    val issueDate: LocalDate = LocalDate.now()
    val fee: BigDecimal = paymentAmount * Constants.FEE_RATE
    val feeRate: BigDecimal = Constants.FEE_RATE
    val taxAmount: BigDecimal = fee * Constants.TAX_RATE
    val taxRate: BigDecimal = Constants.TAX_RATE
    val totalAmount: BigDecimal = paymentAmount + fee + taxAmount
}
