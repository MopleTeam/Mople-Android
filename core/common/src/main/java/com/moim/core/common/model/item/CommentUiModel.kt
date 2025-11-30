package com.moim.core.common.model.item

import com.moim.core.common.model.Comment
import com.moim.core.common.model.OpenGraph

data class CommentUiModel(
    val comment: Comment,
    val texts: List<CommentTextUiModel>,
    val openGraph: OpenGraph? = null,
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