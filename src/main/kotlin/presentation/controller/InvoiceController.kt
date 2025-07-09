package com.example.presentation.controller

import com.example.application.usecase.InvoiceRegistrationUseCase
import com.example.application.usecase.InvoiceListUseCase
import com.example.domain.model.PageRequest
import com.example.presentation.dto.InvoiceRegistrationRequest
import com.example.presentation.dto.InvoiceResponse
import com.example.presentation.dto.PaginatedResponse
import com.example.presentation.dto.PaginationInfo
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

    suspend fun getInvoices(
        userId: Int, 
        startDate: String?, 
        endDate: String?, 
        page: Int?,
        size: Int?
    ): PaginatedResponse<InvoiceResponse> {
        val validatedPage = page?.takeIf { it >= 1 } ?: 1
        val validatedSize = size?.takeIf { it in 1..100 } ?: 20
        val pageRequest = PageRequest(validatedPage, validatedSize)
        
        val start = startDate?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
        val end = endDate?.takeIf { it.isNotBlank() }?.let { LocalDate.parse(it) }
        
        if (start != null && end != null && start.isAfter(end)) {
            throw IllegalArgumentException("Start date must be before or equal to end date")
        }
        
        val pageResult = invoiceListUseCase.execute(userId, start, end, pageRequest)
        
        val paginationInfo = PaginationInfo(
            page = pageResult.pageRequest.page,
            limit = pageResult.pageRequest.size,
            total = pageResult.totalElements.toInt(),
            totalPages = pageResult.totalPages,
            hasNext = pageResult.hasNext,
            hasPrevious = pageResult.hasPrevious
        )
        
        return PaginatedResponse(
            data = pageResult.content.map { invoice -> mapToInvoiceResponse(invoice) },
            pagination = paginationInfo
        )
    }
    
    private fun mapToInvoiceResponse(invoice: com.example.domain.model.Invoice): InvoiceResponse {
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
