package com.moim.feature.plandetail.model

import com.moim.core.common.util.parseMentionTagMessage
import com.moim.core.common.util.parseTextWithLinks
import com.moim.core.model.Comment
import com.moim.core.model.User

data class CommentUiModel(
    val comment: Comment,
    val texts: List<CommentTextUiModel>,
    val replays: List<Comment> = emptyList()
) {
    var isDeleted: Boolean = false
}

sealed class CommentTextUiModel(
    open val content: String
) {
    data class PlainText(
        override val content: String
    ) : CommentTextUiModel(content)

    data class MentionText(
        override val content: String
    ) : CommentTextUiModel(content)

    data class HyperLinkText(
        override val content: String,
    ) : CommentTextUiModel(content)
}

fun Comment.createCommentUiModel(): CommentUiModel {
    val commentTextUiModel = content
        .parseTextWithLinks()
        .map { (isWebLink, text) ->
            if (isWebLink) {
                listOf(CommentTextUiModel.HyperLinkText(content = text))
            } else {
                val users = mentions.map {
                    User(
                        userId = it.userId,
                        nickname = it.nickname,
                        profileUrl = it.imageUrl
                    )
                }
                parseCommentText(
                    mentionNames = users.map { it.nickname },
                    message = parseMentionTagMessage(users, text)
                )
            }
        }.flatten()

    return CommentUiModel(this, commentTextUiModel)
}

private fun parseCommentText(
    mentionNames: List<String>,
    message: String
): List<CommentTextUiModel> {
    if (mentionNames.isEmpty() || message.isEmpty()) {
        return listOf(CommentTextUiModel.PlainText(message))
    }

    // 멘션된 이름들을 길이 순으로 내림차순 정렬 (긴 이름부터 매칭하여 부분 매칭 방지)
    val sortedMentionNames = mentionNames.sortedByDescending { it.length }

    // 정규식 패턴 생성: @이름 형태를 찾기 위한 패턴
    val mentionPattern = sortedMentionNames
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