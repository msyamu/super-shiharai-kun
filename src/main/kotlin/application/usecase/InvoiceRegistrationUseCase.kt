package com.example.application.usecase

import com.example.domain.model.Invoice
import com.example.domain.model.NewInvoice
import com.example.domain.repository.InvoiceRepository
import com.example.presentation.dto.InvoiceRegistrationRequest
import java.math.BigDecimal
import java.time.LocalDate

class InvoiceRegistrationUseCase(
    private val invoiceRepository: InvoiceRepository
) {
    suspend fun execute(userId: Int, request: InvoiceRegistrationRequest): Invoice {
        val paymentAmount = BigDecimal(request.paymentAmount)
        val paymentDueDate = LocalDate.parse(request.paymentDueDate)
        val newInvoice = NewInvoice(
            userId = userId,
            paymentAmount = paymentAmount,
            paymentDueDate = paymentDueDate
        )

        return invoiceRepository.create(newInvoice)
    }
}
