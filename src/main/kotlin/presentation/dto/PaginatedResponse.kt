package com.example.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val data: List<T>,
    val pagination: PaginationInfo
)

@Serializable
data class PaginationInfo(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

data class PaginationParams(
    val page: Int = 1,
    val limit: Int = 50
) {
    val offset: Int get() = (page - 1) * limit
    
    init {
        require(page >= 1) { "Page must be >= 1" }
        require(limit in 1..100) { "Limit must be between 1 and 100" }
    }
}