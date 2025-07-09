package com.example.infrastructure.repository

import com.example.domain.model.Invoice
import com.example.domain.model.NewInvoice
import com.example.domain.repository.InvoiceRepository
import com.example.infrastructure.database.InvoiceTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class InvoiceRepositoryImpl : InvoiceRepository {
    override suspend fun create(newInvoice: NewInvoice): Invoice = transaction {
        val insertedRow = InvoiceTable.insert {
            it[userId] = newInvoice.userId
            it[issueDate] = newInvoice.issueDate
            it[paymentAmount] = newInvoice.paymentAmount
            it[fee] = newInvoice.fee
            it[feeRate] = newInvoice.feeRate
            it[taxAmount] = newInvoice.taxAmount
            it[taxRate] = newInvoice.taxRate
            it[totalAmount] = newInvoice.totalAmount
            it[paymentDueDate] = newInvoice.paymentDueDate
        }
        
        val insertedId = insertedRow[InvoiceTable.id].value
        
        InvoiceTable.selectAll().where { InvoiceTable.id eq insertedId }
            .single().let { resultRow ->
                Invoice(
                    id = resultRow[InvoiceTable.id].value,
                    userId = resultRow[InvoiceTable.userId],
                    issueDate = resultRow[InvoiceTable.issueDate],
                    paymentAmount = resultRow[InvoiceTable.paymentAmount],
                    fee = resultRow[InvoiceTable.fee],
                    feeRate = resultRow[InvoiceTable.feeRate],
                    taxAmount = resultRow[InvoiceTable.taxAmount],
                    taxRate = resultRow[InvoiceTable.taxRate],
                    totalAmount = resultRow[InvoiceTable.totalAmount],
                    paymentDueDate = resultRow[InvoiceTable.paymentDueDate],
                    createdAt = resultRow[InvoiceTable.createdAt],
                    updatedAt = resultRow[InvoiceTable.updatedAt]
                )
            }
    }
}