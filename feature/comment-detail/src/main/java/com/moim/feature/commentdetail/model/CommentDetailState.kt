package com.moim.feature.commentdetail.model

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import com.moim.core.common.model.Comment
import com.moim.core.common.model.User
import com.moim.core.common.model.item.CommentUiModel
import com.moim.core.ui.view.PagingUiState

@Stable
data class CommentDetailState(
    val user: User = User(""),
    val parentComment: CommentUiModel,
    val replyComments: List<CommentUiModel> = emptyList(),
    val pagingInfo: PagingUiState = PagingUiState(),
    val isNotFoundError: Boolean = false,
    val commentState: TextFieldState = TextFieldState(),
    val meetingParticipants: List<User> = emptyList(),
    val searchMentions: List<User> = emptyList(),
    val selectedMentions: List<User> = emptyList(),
    val selectedComment: Comment? = null,
    val selectedUpdateComment: Comment? = null,
    val isShowCommentEditDialog: Boolean = false,
    val isShowCommentReportDialog: Boolean = false,
    val isShowMentionDialog: Boolean = false,
) {
    val isSuccess
        get() = pagingInfo.isSuccess

    val isError
        get() = pagingInfo.isError

    val isLoading
        get() = pagingInfo.isLoading
}
