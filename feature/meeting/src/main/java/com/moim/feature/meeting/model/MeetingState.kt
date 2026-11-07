package com.moim.feature.meeting.model

import androidx.compose.runtime.Immutable
import com.moim.core.common.model.User
import com.moim.core.ui.view.PagingUiState

@Immutable
data class MeetingState(
    val user: User = User(""),
    val meetings: List<MeetingUiModel> = emptyList(),
    val pagingInfo: PagingUiState = PagingUiState(),
) {
    val isSuccess
        get() = pagingInfo.isSuccess

    val isError
        get() = pagingInfo.isError

    val isLoading
        get() = pagingInfo.isLoading
}
