package com.example.domain.model

data class PageRequest(
    val page: Int = MIN_PAGE_NUMBER,
    val size: Int = DEFAULT_PAGE_SIZE
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 20
        const val MAX_PAGE_SIZE = 100
        const val MIN_PAGE_NUMBER = 1
    }
    
    val offset: Int get() = (page - 1) * size
    
    init {
        require(page >= MIN_PAGE_NUMBER) { "Page must be >= $MIN_PAGE_NUMBER" }
        require(size in 1..MAX_PAGE_SIZE) { "Size must be between 1 and $MAX_PAGE_SIZE" }
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