package com.moim.feature.meetingnoticedetail.model

import com.moim.core.common.model.NoticeComment
import com.moim.core.ui.mvi.Intent

sealed interface MeetingNoticeDetailIntent : Intent {
    data object BackClick : MeetingNoticeDetailIntent

    data object RefreshClick : MeetingNoticeDetailIntent

    data object NextCommentsPageLoad : MeetingNoticeDetailIntent

    data object CommentUploadClick : MeetingNoticeDetailIntent

    data class CommentWebLinkClick(
        val webLink: String,
    ) : MeetingNoticeDetailIntent

    data class NoticeEditDialogShow(
        val isShow: Boolean,
    ) : MeetingNoticeDetailIntent

    data object NoticeUpdateClick : MeetingNoticeDetailIntent

    data object NoticeDeleteClick : MeetingNoticeDetailIntent

    data class CommentEditDialogShow(
        val isShow: Boolean,
        val comment: NoticeComment? = null,
    ) : MeetingNoticeDetailIntent

    data class CommentReportDialogShow(
        val isShow: Boolean,
        val comment: NoticeComment? = null,
    ) : MeetingNoticeDetailIntent

    data class CommentUpdateClick(
        val comment: NoticeComment,
    ) : MeetingNoticeDetailIntent

    data class CommentDeleteClick(
        val comment: NoticeComment,
    ) : MeetingNoticeDetailIntent

    data class CommentReportClick(
        val comment: NoticeComment,
    ) : MeetingNoticeDetailIntent
}
