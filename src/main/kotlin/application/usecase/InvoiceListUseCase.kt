package com.example.application.usecase

import com.example.domain.model.Invoice
import com.example.domain.model.Page
import com.example.domain.model.PageRequest
import com.example.domain.repository.InvoiceRepository
import java.time.LocalDate

class InvoiceListUseCase(
    private val invoiceRepository: InvoiceRepository
) {
    suspend fun execute(
        userId: Int, 
        startDate: LocalDate?, 
        endDate: LocalDate?, 
        pageRequest: PageRequest
    ): Page<Invoice> {
        return invoiceRepository.findByUserIdWithOptionalDateRange(userId, startDate, endDate, pageRequest)
    }
}
