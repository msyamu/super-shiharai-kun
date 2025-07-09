package com.example.domain.repository

import com.example.domain.model.Invoice
import com.example.domain.model.NewInvoice

interface InvoiceRepository {
    suspend fun create(newInvoice: NewInvoice): Invoice
}