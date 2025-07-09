package com.example.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class InvoiceResponse(
    val id: Int,
    val userId: Int,
    val issueDate: String,
    val paymentAmount: String,
    val fee: String,
    val feeRate: String,
    val taxAmount: String,
    val taxRate: String,
    val totalAmount: String,
    val paymentDueDate: String,
    val createdAt: String,
    val updatedAt: String
)
