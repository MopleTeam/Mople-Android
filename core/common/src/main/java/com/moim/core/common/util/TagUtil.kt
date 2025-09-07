package com.moim.core.common.util

import com.moim.core.model.User

fun createMentionTagMessage(
    mentionUsers: List<User>,
    message: String
): String {
    var result = message
    mentionUsers.forEach { user ->
        val mentionPattern = "@${user.nickname}"
        result = result.replace(mentionPattern, "<mention id=${user.userId}>$mentionPattern</mention>")
    }

    return result
}

fun parseMentionTagMessage(
    mentionUsers: List<User>,
    message: String
): String {
    val userMap = mentionUsers.associateBy { it.userId }
    val mentionPattern = Regex("""<mention id=(\d+)>@[^<]+</mention>""")

    return mentionPattern.replace(message) { matchResult ->
        val userId = matchResult.groupValues.getOrNull(1)

        userMap[userId]
            ?.let { user -> "@${user.nickname}" }
            ?: matchResult.value
    }
}

fun filterMentionedUsers(
    mentionUsers: List<User>,
    message: String
): List<User> {
    if (mentionUsers.isEmpty() || message.isEmpty()) {
        return emptyList()
    }

    // 메시지에서 멘션된 사용자 ID들 추출
    val mentionPattern = Regex("""<mention id=(\d+)>@[^<]+</mention>""")
    val mentionedUserIds = mentionPattern.findAll(message)
        .map { it.groupValues[1] }
        .toSet() // 중복 제거

    // 실제로 멘션된 사용자들만 필터링
    return mentionUsers.filter { user ->
        user.userId in mentionedUserIds
    }
}
