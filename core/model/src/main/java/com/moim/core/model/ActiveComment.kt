package com.moim.core.model

import androidx.compose.runtime.Stable
import com.moim.core.data.model.ActiveCommentResponse

@Stable
data class ActiveComment(
    val id: String = "",
    val creatorId: String = "",
    val creatorNickname: String = "",
    val creatorProfileUrl: String = "",
    val contents: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

fun ActiveCommentResponse.asItem(): ActiveComment {
    return ActiveComment(
        id = id,
        creatorId = creatorId,
        creatorNickname = creatorNickname,
        creatorProfileUrl = creatorProfileUrl,
        contents = contents,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}