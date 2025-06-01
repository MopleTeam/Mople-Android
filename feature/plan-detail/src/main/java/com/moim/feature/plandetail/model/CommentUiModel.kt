package com.moim.feature.plandetail.model

import com.moim.core.model.Comment

data class CommentUiModel(
    val comment: Comment,
    val texts: List<CommentTextUiModel>
)

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
