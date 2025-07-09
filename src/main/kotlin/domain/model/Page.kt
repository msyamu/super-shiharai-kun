package com.example.domain.model

data class PageRequest(
    val page: Int = 1,
    val size: Int = 20
) {
    val offset: Int get() = (page - 1) * size
    
    init {
        require(page >= 1) { "Page must be >= 1" }
        require(size in 1..100) { "Size must be between 1 and 100" }
    }
}

data class Page<T>(
    val content: List<T>,
    val totalElements: Long,
    val pageRequest: PageRequest
) {
    val totalPages: Int get() = ((totalElements + pageRequest.size - 1) / pageRequest.size).toInt()
    val hasNext: Boolean get() = pageRequest.page < totalPages
    val hasPrevious: Boolean get() = pageRequest.page > 1
    val isFirst: Boolean get() = pageRequest.page == 1
    val isLast: Boolean get() = pageRequest.page == totalPages
}