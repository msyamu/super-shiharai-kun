package com.example.infrastructure.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

object InvoiceTable : IntIdTable("invoices") {
    val userId = integer("user_id").references(UserTable.id).index()
    val issueDate = date("issue_date")
    val paymentAmount = decimal("payment_amount", 15, 2)
    val fee = decimal("fee", 15, 2)
    val feeRate = decimal("fee_rate", 5, 2)
    val taxAmount = decimal("tax_amount", 15, 2)
    val taxRate = decimal("tax_rate", 5, 2)
    val totalAmount = decimal("total_amount", 15, 2)
    val paymentDueDate = date("payment_due_date").index()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}
