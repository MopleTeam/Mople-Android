package com.moim.feature.plandetail.model

import com.moim.core.common.model.Comment
import com.moim.core.common.model.User
import com.moim.core.ui.mvi.Intent

sealed interface PlanDetailIntent : Intent {
    data object BackClick : PlanDetailIntent

    data object RefreshClick : PlanDetailIntent

    data object ParticipantsClick : PlanDetailIntent

    data object PlanDeleteClick : PlanDetailIntent

    data object PlanUpdateClick : PlanDetailIntent

    data object PlanReportClick : PlanDetailIntent

    data object MapDetailClick : PlanDetailIntent

    data object NextCommentsPageLoad : PlanDetailIntent

    data class PlanApplyClick(
        val isApply: Boolean,
    ) : PlanDetailIntent

    data class CommentLikeClick(
        val comment: Comment,
    ) : PlanDetailIntent

    data class CommentAddReplyClick(
        val comment: Comment,
    ) : PlanDetailIntent

    data class CommentReportClick(
        val comment: Comment,
    ) : PlanDetailIntent

    data class CommentUpdateClick(
        val comment: Comment,
    ) : PlanDetailIntent

    data class CommentDeleteClick(
        val comment: Comment,
    ) : PlanDetailIntent

    data class CommentUploadClick(
        val updateComment: Comment?,
    ) : PlanDetailIntent

    data class CommentWebLinkClick(
        val webLink: String,
    ) : PlanDetailIntent

    data class ReviewImageClick(
        val selectedImageIndex: Int,
    ) : PlanDetailIntent

    data class UserProfileImageClick(
        val imageUrl: String,
        val userName: String,
    ) : PlanDetailIntent

    data class MentionUserClick(
        val user: User,
    ) : PlanDetailIntent

    data class MentionDialogShow(
        val keyword: String?,
    ) : PlanDetailIntent

    data class PlanApplyCancelDialogShow(
        val isShow: Boolean,
    ) : PlanDetailIntent

    data class PlanEditDialogShow(
        val isShow: Boolean,
    ) : PlanDetailIntent

    data class PlanReportDialogShow(
        val isShow: Boolean,
    ) : PlanDetailIntent

    data class CommentEditDialogShow(
        val isShow: Boolean,
        val comment: Comment?,
    ) : PlanDetailIntent

    data class CommentReportDialogShow(
        val isShow: Boolean,
        val comment: Comment?,
    ) : PlanDetailIntent
}
