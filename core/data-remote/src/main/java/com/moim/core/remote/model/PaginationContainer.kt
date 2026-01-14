package com.moim.core.remote.model

import com.moim.core.common.model.CursorPage
import com.moim.core.common.model.PaginationContainer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationContainerResponse<T>(
    @SerialName("totalCount")
    val totalCount: Int = 0,
    @SerialName("content")
    val content: T,
    @SerialName("page")
    val page: CursorPagerResponse,
)

@Serializable
data class CursorPagerResponse(
    @SerialName("nextCursor")
    val nextCursor: String?,
    @SerialName("hasNext")
    val isNext: Boolean,
    @SerialName("size")
    val size: Int,
)

fun <T, R> PaginationContainerResponse<T>.asItem(transform: (T) -> R): PaginationContainer<R> =
    PaginationContainer(
        totalCount = totalCount,
        content = content.let(transform),
        page = page.asItem(),
    )

fun CursorPagerResponse.asItem(): CursorPage =
    CursorPage(
        nextCursor = nextCursor,
        isNext = isNext,
        size = size,
    )
