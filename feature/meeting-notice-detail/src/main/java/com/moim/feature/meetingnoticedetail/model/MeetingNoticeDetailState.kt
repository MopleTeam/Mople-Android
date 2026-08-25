package com.moim.feature.meetingnoticedetail.model

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import com.moim.core.common.model.NoticeComment
import com.moim.core.common.model.User
import com.moim.core.common.model.item.NoticeCommentUiModel
import com.moim.core.common.result.Result
import com.moim.core.ui.view.PagingUiState

@Stable
data class MeetingNoticeDetailState(
    val user: User = User(""),
    val notice: Result<NoticeUiModel> = Result.Loading,
    val isNotFoundError: Boolean = false,
    val isHostUser: Boolean = false,
    val isShowNoticeEditDialog: Boolean = false,
    val isShowCommentEditDialog: Boolean = false,
    val isShowCommentReportDialog: Boolean = false,
    val selectedComment: NoticeComment? = null,
    val selectedUpdateComment: NoticeComment? = null,
    val commentState: TextFieldState = TextFieldState(),
    val comments: List<NoticeCommentUiModel> = emptyList(),
    val commentsPagingInfo: PagingUiState = PagingUiState(),
) {
    val isSuccess
        get() = notice is Result.Success

    val isError
        get() = notice is Result.Error

    val isLoading
        get() = notice is Result.Loading
}
