package com.moim.core.common.model.item

import com.moim.core.common.model.NoticeComment
import com.moim.core.common.model.OpenGraph

data class NoticeCommentUiModel(
    val commentId: String = "",
    val comment: NoticeComment,
    val texts: List<NoticeCommentTextUiModel>,
    val openGraph: OpenGraph? = null,
)

sealed class NoticeCommentTextUiModel(
    open val content: String,
) {
    data class PlainText(
        override val content: String,
    ) : NoticeCommentTextUiModel(content)

    data class HyperLinkText(
        override val content: String,
    ) : NoticeCommentTextUiModel(content)
}
