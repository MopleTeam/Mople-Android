package com.moim.feature.participantlistforleaderchange.model

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import com.moim.core.common.model.User
import com.moim.core.ui.view.PagingUiState

@Stable
data class ParticipantListForLeaderChangeState(
    val keywordState: TextFieldState = TextFieldState(),
    val users: List<ParticipantListUiModel> = emptyList(),
    val selectedUser: User? = null,
    val isShowChangeUserDialog: Boolean = false,
    val pagingInfo: PagingUiState = PagingUiState(),
) {
    val isSuccess
        get() = pagingInfo.isSuccess

    val isError
        get() = pagingInfo.isError

    val isLoading
        get() = pagingInfo.isLoading
}
