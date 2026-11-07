package com.moim.feature.commentdetail.model

import com.moim.core.common.model.Comment
import com.moim.core.common.model.User
import com.moim.core.ui.mvi.Intent

sealed interface CommentDetailIntent : Intent {
    data object BackClick : CommentDetailIntent

    data object RefreshClick : CommentDetailIntent

    data object NextPageLoad : CommentDetailIntent

    data class MentionUserClick(
        val user: User,
    ) : CommentDetailIntent

    data class UserProfileImageClick(
        val imageUrl: String,
        val userName: String,
    ) : CommentDetailIntent

    data class CommentLikeClick(
        val comment: Comment,
    ) : CommentDetailIntent

    data class CommentReportClick(
        val comment: Comment,
    ) : CommentDetailIntent

    data class CommentUpdateClick(
        val comment: Comment,
    ) : CommentDetailIntent

    data class CommentDeleteClick(
        val comment: Comment,
    ) : CommentDetailIntent

    data class CommentUploadClick(
        val updateComment: Comment?,
    ) : CommentDetailIntent

    data class CommentWebLinkClick(
        val webLink: String,
    ) : CommentDetailIntent

    data class MentionDialogShow(
        val keyword: String?,
    ) : CommentDetailIntent

    data class CommentEditDialogShow(
        val isShow: Boolean,
        val comment: Comment?,
    ) : CommentDetailIntent

    data class CommentReportDialogShow(
        val isShow: Boolean,
        val comment: Comment?,
    ) : CommentDetailIntent
}
