package com.example.domain.model

import java.time.LocalDateTime

data class User(
    val id: Int,
    val companyName: String,
    val name: String,
    val email: String,
    val password: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
