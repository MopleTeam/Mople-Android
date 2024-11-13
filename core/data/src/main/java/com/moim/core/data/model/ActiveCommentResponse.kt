package com.moim.core.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActiveCommentResponse(
    @SerialName("id")
    val id: String,
    @SerialName("creatorId")
    val creatorId: String,
    @SerialName("creatorNickname")
    val creatorNickname: String,
    @SerialName("creatorProfileImgUrl")
    val creatorProfileUrl: String,
    @SerialName("contents")
    val contents: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("modifiedAt")
    val updatedAt: String
)