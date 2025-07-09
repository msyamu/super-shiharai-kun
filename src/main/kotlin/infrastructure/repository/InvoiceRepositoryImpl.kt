package com.example.infrastructure.repository

import com.example.domain.model.Invoice
import com.example.domain.model.NewInvoice
import com.example.domain.repository.InvoiceRepository
import com.example.infrastructure.database.InvoiceTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
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
            .single().let { rowToInvoice(it) }
    }

    override suspend fun findByUserIdWithOptionalDateRange(userId: Int, startDate: LocalDate?, endDate: LocalDate?): List<Invoice> = transaction {
        var query = InvoiceTable.selectAll().where { InvoiceTable.userId eq userId }

        startDate?.let { start ->
            query = query.andWhere { InvoiceTable.paymentDueDate greaterEq start }
        }

        endDate?.let { end ->
            query = query.andWhere { InvoiceTable.paymentDueDate lessEq end }
        }

        query.map { rowToInvoice(it) }
    }

    private fun rowToInvoice(row: ResultRow): Invoice {
        return Invoice(
            id = row[InvoiceTable.id].value,
            userId = row[InvoiceTable.userId],
            issueDate = row[InvoiceTable.issueDate],
            paymentAmount = row[InvoiceTable.paymentAmount],
            fee = row[InvoiceTable.fee],
            feeRate = row[InvoiceTable.feeRate],
            taxAmount = row[InvoiceTable.taxAmount],
            taxRate = row[InvoiceTable.taxRate],
            totalAmount = row[InvoiceTable.totalAmount],
            paymentDueDate = row[InvoiceTable.paymentDueDate],
            createdAt = row[InvoiceTable.createdAt],
            updatedAt = row[InvoiceTable.updatedAt]
        )
    }
}
