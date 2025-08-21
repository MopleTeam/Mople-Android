package com.moim.core.data.mapper

import com.moim.core.datamodel.MemberResponse
import com.moim.core.model.Member

fun MemberResponse.asItem(): Member {
    return Member(
        userNickname = userNickname,
        imageUrl = imageUrl
    )
}