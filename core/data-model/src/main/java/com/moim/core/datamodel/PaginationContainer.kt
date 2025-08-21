package com.moim.core.datamodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationContainerResponse<T>(
    @SerialName("totalCount")
    val totalCount: Int = 0,
    @SerialName("content")
    val content: T,
    @SerialName("page")
    val page: CursorPagerResponse
)

@Serializable
data class CursorPagerResponse(
    @SerialName("nextCursor")
    val nextCursor: String?,
    @SerialName("hasNext")
    val isNext: Boolean,
    @SerialName("size")
    val size: Int
)
