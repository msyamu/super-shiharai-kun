package com.example.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserRegistrationRequest(
    val companyName: String,
    val name: String,
    val email: String,
    val password: String
)
