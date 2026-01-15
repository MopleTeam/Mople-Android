package com.moim.core.common.model

data class PaginationContainer<T>(
    val totalCount: Int,
    val content: T,
    val page: CursorPage,
)

data class CursorPage(
    val nextCursor: String?,
    val isNext: Boolean,
    val size: Int,
)
