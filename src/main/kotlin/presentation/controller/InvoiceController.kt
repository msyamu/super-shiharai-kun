package com.example.presentation.controller

import com.example.application.usecase.InvoiceRegistrationUseCase
import com.example.domain.model.NewInvoice
import com.example.presentation.dto.InvoiceRegistrationRequest
import com.example.presentation.dto.InvoiceResponse
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InvoiceController(
    private val invoiceRegistrationUseCase: InvoiceRegistrationUseCase
) {
    suspend fun registerInvoice(userId: Int, request: InvoiceRegistrationRequest): InvoiceResponse {
        val invoice = invoiceRegistrationUseCase.execute(
            userId = userId,
            paymentAmount = BigDecimal(request.paymentAmount),
            paymentDueDate = LocalDate.parse(request.paymentDueDate)
        )

        return InvoiceResponse(
            id = invoice.id,
            userId = invoice.userId,
            issueDate = invoice.issueDate.toString(),
            paymentAmount = invoice.paymentAmount.toString(),
            fee = invoice.fee.toString(),
            feeRate = invoice.feeRate.toString(),
            taxAmount = invoice.taxAmount.toString(),
            taxRate = invoice.taxRate.toString(),
            totalAmount = invoice.totalAmount.toString(),
            paymentDueDate = invoice.paymentDueDate.toString(),
            createdAt = invoice.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            updatedAt = invoice.updatedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }
}