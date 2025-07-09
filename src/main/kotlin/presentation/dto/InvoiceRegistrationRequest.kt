package com.example.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class InvoiceRegistrationRequest(
    val paymentAmount: String,
    val paymentDueDate: String
)