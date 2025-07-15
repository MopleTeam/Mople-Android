package com.moim.core.model

data class PaginationContainer<T>(
    val content: T,
    val cursorPage: CursorPage
)

data class CursorPage(
    val nextCursor: String?,
    val isNext: Boolean,
    val size: Int
)