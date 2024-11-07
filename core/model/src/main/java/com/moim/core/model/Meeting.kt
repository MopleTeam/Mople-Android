package com.moim.core.model

import androidx.compose.runtime.Stable
import com.moim.core.data.model.MeetingResponse
import com.moim.core.data.model.MemberResponse

@Stable
data class Meeting(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val members: List<Member> = emptyList(),
    val creatorId: String = "",
    val creatorNickname: String = "",
    val createdAt: String = "",
    val lastPlanAt: String = ""
)

@Stable
data class Member(
    val id: String = "",
    val userId: String = "",
    val userNickname: String = "",
    val joinedAt: String = ""
)

fun MeetingResponse.asItem(): Meeting {
    return Meeting(
        id = id,
        name = name,
        imageUrl = imageUrl,
        members = members.map(MemberResponse::asItem),
        creatorId = creatorId,
        creatorNickname = creatorNickname,
        createdAt = createdAt,
        lastPlanAt = lastPlanAt
    )
}

fun MemberResponse.asItem(): Member {
    return Member(
        id = id,
        userId = userId,
        userNickname = userNickname,
        joinedAt = joinedAt
    )
}