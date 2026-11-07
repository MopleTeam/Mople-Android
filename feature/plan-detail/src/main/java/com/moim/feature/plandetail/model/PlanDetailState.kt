package com.moim.feature.plandetail.model

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import com.moim.core.common.model.Comment
import com.moim.core.common.model.User
import com.moim.core.common.model.item.CommentUiModel
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.result.Result
import com.moim.core.ui.view.PagingUiState

@Stable
data class PlanDetailState(
    val user: User = User(""),
    val planItem: Result<PlanItem> = Result.Loading,
    val isNotFoundError: Boolean = false,
    val commentState: TextFieldState = TextFieldState(),
    val comments: List<CommentUiModel> = emptyList(),
    val commentsPagingInfo: PagingUiState = PagingUiState(),
    val meetingParticipants: List<User> = emptyList(),
    val searchMentions: List<User> = emptyList(),
    val selectedMentions: List<User> = emptyList(),
    val selectedImageIndex: Int = 0,
    val selectedComment: Comment? = null,
    val selectedUpdateComment: Comment? = null,
    val isShowApplyButton: Boolean = false,
    val isShowApplyCancelDialog: Boolean = false,
    val isShowPlanEditDialog: Boolean = false,
    val isShowPlanReportDialog: Boolean = false,
    val isShowCommentEditDialog: Boolean = false,
    val isShowCommentReportDialog: Boolean = false,
    val isShowMentionDialog: Boolean = false,
) {
    val isSuccess
        get() = planItem is Result.Success

    val isError
        get() = planItem is Result.Error

    val isLoading
        get() = planItem is Result.Loading
}
