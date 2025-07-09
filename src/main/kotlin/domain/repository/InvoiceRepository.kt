package com.example.domain.repository

import com.example.domain.model.Invoice
import com.example.domain.model.NewInvoice
import java.time.LocalDate

interface InvoiceRepository {
    suspend fun create(newInvoice: NewInvoice): Invoice
    suspend fun findByUserIdWithOptionalDateRange(userId: Int, startDate: LocalDate?, endDate: LocalDate?): List<Invoice>
}
