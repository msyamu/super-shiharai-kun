package com.example.domain.repository

import com.example.domain.model.Invoice
import com.example.domain.model.NewInvoice
import com.example.domain.model.Page
import com.example.domain.model.PageRequest
import java.time.LocalDate

interface InvoiceRepository {
    suspend fun create(newInvoice: NewInvoice): Invoice
    suspend fun findByUserIdWithOptionalDateRange(userId: Int, startDate: LocalDate?, endDate: LocalDate?, pageRequest: PageRequest): Page<Invoice>
}
