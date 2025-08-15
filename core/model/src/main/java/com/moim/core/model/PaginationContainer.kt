package com.moim.core.model

data class PaginationContainer<T>(
    val content: T,
    val page: CursorPage
)

data class CursorPage(
    val nextCursor: String?,
    val isNext: Boolean,
    val size: Int
)