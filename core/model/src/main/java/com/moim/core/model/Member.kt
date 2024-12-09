package com.moim.core.model

import androidx.compose.runtime.Stable
import com.moim.core.datamodel.MemberResponse

@Stable
data class Member(
    val userNickname: String = "",
    val imageUrl: String? = null,
)

fun MemberResponse.asItem(): Member {
    return Member(
        userNickname = userNickname,
        imageUrl = imageUrl
    )
}