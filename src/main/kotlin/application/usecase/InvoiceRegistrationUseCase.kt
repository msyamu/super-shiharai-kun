package com.example.application.usecase

import com.example.domain.model.Invoice
import com.example.domain.model.NewInvoice
import com.example.domain.repository.InvoiceRepository
import com.example.presentation.dto.InvoiceRegistrationRequest

class InvoiceRegistrationUseCase(
    private val invoiceRepository: InvoiceRepository
) {
    suspend fun execute(userId: Int, request: InvoiceRegistrationRequest): Invoice {
        val newInvoice = NewInvoice(
            userId = userId,
            paymentAmount = request.paymentAmount,
            paymentDueDate = request.paymentDueDate
        )

        return invoiceRepository.create(newInvoice)
    }
}
