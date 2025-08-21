package com.moim.feature.plandetail.model

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

    data class HyperLinkText(
        override val content: String,
        val startIndex: Int,
        val endIndex: Int
    ) : CommentTextUiModel(content)
}

fun Comment.createCommentUiModel(): CommentUiModel {
    val commentTextUiModel = content
        .parseTextWithLinks()
        .map { (isWebLink, text) ->
            if (isWebLink) {
                val startIndex = content.indexOf(text)
                val endIndex = startIndex + text.length

                CommentTextUiModel.HyperLinkText(
                    content = text,
                    startIndex = startIndex,
                    endIndex = endIndex
                )
            } else {
                CommentTextUiModel.PlainText(text)
            }
        }

    return CommentUiModel(this, commentTextUiModel)
}
