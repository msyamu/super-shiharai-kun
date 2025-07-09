package com.example.presentation.controller

import com.example.application.usecase.InvoiceRegistrationUseCase
import com.example.application.usecase.InvoiceListUseCase
import com.example.presentation.dto.InvoiceRegistrationRequest
import com.example.presentation.dto.InvoiceResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InvoiceController(
    private val invoiceRegistrationUseCase: InvoiceRegistrationUseCase,
    private val invoiceListUseCase: InvoiceListUseCase
) {
    suspend fun registerInvoice(userId: Int, request: InvoiceRegistrationRequest): InvoiceResponse {
        val invoice = invoiceRegistrationUseCase.execute(userId, request)

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

    suspend fun getInvoices(userId: Int, startDate: String?, endDate: String?): List<InvoiceResponse> {
        val start = startDate?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
        val end = endDate?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
        
        if (start != null && end != null && start.isAfter(end)) {
            throw IllegalArgumentException("Start date must be before or equal to end date")
        }
        
        val invoices = invoiceListUseCase.execute(userId, start, end)

        return invoices.map { invoice ->
            InvoiceResponse(
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
}
