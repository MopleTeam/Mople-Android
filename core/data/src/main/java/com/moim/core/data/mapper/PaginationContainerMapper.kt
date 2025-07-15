package com.moim.core.data.mapper

import com.moim.core.datamodel.CursorPagerResponse
import com.moim.core.datamodel.PaginationContainerResponse
import com.moim.core.model.CursorPage
import com.moim.core.model.PaginationContainer


fun <T, R> PaginationContainerResponse<T>.asItem(transform: (T) -> R): PaginationContainer<R> =
    PaginationContainer(
        content = content.let(transform),
        cursorPage = cursorPage.asItem()
    )

fun CursorPagerResponse.asItem(): CursorPage {
    return CursorPage(
        nextCursor = nextCursor,
        isNext = isNext,
        size = size
    )
}
