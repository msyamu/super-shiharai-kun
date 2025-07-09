package com.example.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,
    val companyName: String,
    val name: String,
    val email: String
)