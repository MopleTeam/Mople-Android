package com.moim.feature.plandetail.model

import com.moim.core.common.util.parseMentionTagMessage
import com.moim.core.common.util.parseTextWithLinks
import com.moim.core.model.Comment

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
                val names = mentions.map { it.nickname }
                parseCommentText(
                    mentionNames = names,
                    message = parseMentionTagMessage(names, text)
                )
            }
        }.flatten()

    return CommentUiModel(this, commentTextUiModel)
}


private fun parseCommentText(mentionNames: List<String>, message: String): List<CommentTextUiModel> {
    val result = mutableListOf<CommentTextUiModel>()
    var currentIndex = 0

    // 모든 멘션 패턴과 위치를 찾기
    val mentionPositions = mutableListOf<Triple<Int, Int, String>>()

    mentionNames.forEach { nickname ->
        val mentionPattern = "@${nickname}"
        var searchIndex = 0

        while (true) {
            val foundIndex = message.indexOf(mentionPattern, searchIndex)
            if (foundIndex == -1) break

            mentionPositions.add(Triple(foundIndex, foundIndex + mentionPattern.length, mentionPattern))
            searchIndex = foundIndex + 1
        }
    }

    mentionPositions.sortBy { it.first }

    mentionPositions.forEach { (startIndex, endIndex, mentionText) ->
        if (currentIndex < startIndex) {
            val plainText = message.substring(currentIndex, startIndex)
            if (plainText.isNotEmpty()) {
                result.add(CommentTextUiModel.PlainText(plainText))
            }
        }

        result.add(CommentTextUiModel.MentionText(mentionText))
        currentIndex = endIndex
    }

    if (currentIndex < message.length) {
        val remainingText = message.substring(currentIndex)
        if (remainingText.isNotEmpty()) {
            result.add(CommentTextUiModel.PlainText(remainingText))
        }
    }

    return result
}