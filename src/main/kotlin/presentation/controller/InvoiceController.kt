package com.example.presentation.controller

import com.example.application.usecase.InvoiceRegistrationUseCase
import com.example.application.usecase.InvoiceListUseCase
import com.example.domain.model.Invoice
import com.example.domain.model.Page
import com.example.domain.model.PageRequest
import com.example.infrastructure.config.Constants
import com.example.presentation.dto.InvoiceRegistrationRequest
import com.example.presentation.dto.InvoiceResponse
import com.example.presentation.dto.PaginatedResponse
import com.example.presentation.dto.PaginationInfo
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class InvoiceController(
    private val invoiceRegistrationUseCase: InvoiceRegistrationUseCase,
    private val invoiceListUseCase: InvoiceListUseCase
) {
    suspend fun registerInvoice(userId: Int, request: InvoiceRegistrationRequest): InvoiceResponse {
        val invoice = invoiceRegistrationUseCase.execute(userId, request)
        return invoice.toResponse()
    }

    suspend fun getInvoices(
        userId: Int, 
        startDate: String?, 
        endDate: String?, 
        page: Int?,
        size: Int?
    ): PaginatedResponse<InvoiceResponse> {
        val pageRequest = createPageRequest(page, size)
        val dateRange = parseDateRange(startDate, endDate)
        val pageResult = invoiceListUseCase.execute(userId, dateRange.first, dateRange.second, pageRequest)
        return createPaginatedResponse(pageResult)
    }
    
    private fun createPageRequest(page: Int?, size: Int?): PageRequest {
        val validatedPage = page ?: PageRequest.MIN_PAGE_NUMBER
        val validatedSize = size ?: PageRequest.DEFAULT_PAGE_SIZE
        
        if (validatedPage < PageRequest.MIN_PAGE_NUMBER) {
            throw IllegalArgumentException("Page number must be at least ${PageRequest.MIN_PAGE_NUMBER}")
        }
        if (validatedSize !in 1..PageRequest.MAX_PAGE_SIZE) {
            throw IllegalArgumentException("Page size must be between 1 and ${PageRequest.MAX_PAGE_SIZE}")
        }
        
        return PageRequest(validatedPage, validatedSize)
    }
    
    private fun parseDateRange(startDate: String?, endDate: String?): Pair<LocalDate?, LocalDate?> {
        if (startDate != null && startDate.isBlank()) {
            throw IllegalArgumentException("Start date cannot be blank")
        }
        if (endDate != null && endDate.isBlank()) {
            throw IllegalArgumentException("End date cannot be blank")
        }
        
        val start = startDate?.let { LocalDate.parse(it) }
        val end = endDate?.let { LocalDate.parse(it) }
        
        if (start != null && end != null && start.isAfter(end)) {
            throw IllegalArgumentException("Start date must be before or equal to end date")
        }
        
        return Pair(start, end)
    }
    
    private fun createPaginatedResponse(pageResult: Page<Invoice>): PaginatedResponse<InvoiceResponse> {
        val paginationInfo = PaginationInfo(
            page = pageResult.pageRequest.page,
            limit = pageResult.pageRequest.size,
            total = pageResult.totalElements.toInt(),
            totalPages = pageResult.totalPages,
            hasNext = pageResult.hasNext,
            hasPrevious = pageResult.hasPrevious
        )
        
        return PaginatedResponse(
            data = pageResult.content.map { it.toResponse() },
            pagination = paginationInfo
        )
    }
    
    private fun Invoice.toResponse(): InvoiceResponse {
        return InvoiceResponse(
            id = id,
            userId = userId,
            issueDate = issueDate.toStringFormat(),
            paymentAmount = paymentAmount.toStringFormat(),
            fee = fee.toStringFormat(),
            feeRate = feeRate.toStringFormat(),
            taxAmount = taxAmount.toStringFormat(),
            taxRate = taxRate.toStringFormat(),
            totalAmount = totalAmount.toStringFormat(),
            paymentDueDate = paymentDueDate.toStringFormat(),
            createdAt = createdAt.toStringFormat(),
            updatedAt = updatedAt.toStringFormat()
        )
    }
    
    private fun BigDecimal.toStringFormat(): String = this.toString()
    private fun LocalDate.toStringFormat(): String = this.toString() 
    private fun LocalDateTime.toStringFormat(): String = this.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}
