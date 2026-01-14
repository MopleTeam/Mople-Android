package com.moim.core.ui.util

import com.moim.core.common.model.Comment
import com.moim.core.common.model.User
import com.moim.core.common.model.item.CommentTextUiModel
import com.moim.core.common.model.item.CommentUiModel

fun createMentionTagMessage(
    mentionUsers: List<User>,
    message: String,
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
    message: String,
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
    message: String,
): List<User> {
    if (mentionUsers.isEmpty() || message.isEmpty()) {
        return emptyList()
    }

    // 메시지에서 멘션된 사용자 ID들 추출
    val mentionPattern = Regex("""<mention id=(\d+)>@[^<]+</mention>""")
    val mentionedUserIds =
        mentionPattern
            .findAll(message)
            .map { it.groupValues[1] }
            .toSet() // 중복 제거

    // 실제로 멘션된 사용자들만 필터링
    return mentionUsers.filter { user ->
        user.userId in mentionedUserIds
    }
}

fun Comment.createCommentUiModel(): CommentUiModel {
    val commentTexts = content.parseTextWithLinks()
    val commentTextUiModel =
        commentTexts
            .map { (isWebLink, text) ->
                if (isWebLink) {
                    if (text == openGraph?.url && commentTexts.size == 1) {
                        emptyList()
                    } else {
                        listOf(CommentTextUiModel.HyperLinkText(content = text))
                    }
                } else {
                    val users =
                        mentions.map {
                            User(
                                userId = it.userId,
                                nickname = it.nickname,
                                profileUrl = it.imageUrl,
                            )
                        }
                    parseCommentText(
                        mentionNames = users.map { it.nickname },
                        message = parseMentionTagMessage(users, text),
                    )
                }
            }.flatten()

    return CommentUiModel(
        comment = this,
        texts = commentTextUiModel,
        openGraph = this.openGraph,
    )
}

private fun parseCommentText(
    mentionNames: List<String>,
    message: String,
): List<CommentTextUiModel> {
    if (mentionNames.isEmpty() || message.isEmpty()) {
        return listOf(CommentTextUiModel.PlainText(message))
    }

    // 멘션된 이름들을 길이 순으로 내림차순 정렬 (긴 이름부터 매칭하여 부분 매칭 방지)
    val sortedMentionNames = mentionNames.sortedByDescending { it.length }

    // 정규식 패턴 생성: @이름 형태를 찾기 위한 패턴
    val mentionPattern =
        sortedMentionNames
            .map { Regex.escape(it) } // 특수문자 이스케이프
            .joinToString("|") { "@$it" }

    val regex = Regex("($mentionPattern)")

    val result = mutableListOf<CommentTextUiModel>()
    var lastIndex = 0

    // 정규식으로 멘션 부분을 찾아서 순차적으로 처리
    regex.findAll(message).forEach { matchResult ->
        val start = matchResult.range.first
        val end = matchResult.range.last + 1

        // 멘션 이전의 일반 텍스트 추가
        if (start > lastIndex) {
            val plainText = message.substring(lastIndex, start)
            if (plainText.isNotEmpty()) {
                result.add(CommentTextUiModel.PlainText(plainText))
            }
        }

        // 멘션 텍스트 추가
        val mentionText = matchResult.value
        result.add(CommentTextUiModel.MentionText(mentionText))

        lastIndex = end
    }

    // 마지막 멘션 이후의 일반 텍스트 추가
    if (lastIndex < message.length) {
        val remainingText = message.substring(lastIndex)
        if (remainingText.isNotEmpty()) {
            result.add(CommentTextUiModel.PlainText(remainingText))
        }
    }

    return result
}
