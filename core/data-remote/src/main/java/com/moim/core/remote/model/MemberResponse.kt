package com.moim.core.remote.model

import com.moim.core.common.model.Member
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberResponse(
    @SerialName("nickname")
    val userNickname: String,
    @SerialName("profileImg")
    val imageUrl: String? = null,
)

fun MemberResponse.asItem(): Member =
    Member(
        userNickname = userNickname,
        imageUrl = imageUrl,
    )
