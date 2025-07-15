package com.moim.core.data.mapper

import com.moim.core.common.util.parseZonedDateTime
import com.moim.core.datamodel.CommentResponse
import com.moim.core.datamodel.WriterResponse
import com.moim.core.model.Comment
import com.moim.core.model.Writer

fun CommentResponse.asItem(): Comment {
    return Comment(
        postId = postId,
        commentId = commentId,
        content = content,
        parentId = parentId,
        replayCount = replayCount ?: 0,
        likeCount = likeCount,
        isLike = isLike,
        writer = writer.asItem(),
        mentions = mentions.map(WriterResponse::asItem),
        commentAt = commentAt.parseZonedDateTime(),
    )
}

fun WriterResponse.asItem(): Writer {
    return Writer(
        userId = userId,
        nickname = nickname,
        imageUrl = image
    )
}
