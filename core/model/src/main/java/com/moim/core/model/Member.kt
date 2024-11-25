package com.moim.core.model

import androidx.compose.runtime.Stable
import com.moim.core.data.model.MemberResponse

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