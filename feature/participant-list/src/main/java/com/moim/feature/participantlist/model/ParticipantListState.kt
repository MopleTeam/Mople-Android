package com.moim.feature.participantlist.model

import androidx.compose.runtime.Immutable
import com.moim.core.common.model.User
import com.moim.core.ui.view.PagingUiState

@Immutable
data class ParticipantListState(
    val isMeeting: Boolean = true,
    val participants: List<User> = emptyList(),
    val pagingInfo: PagingUiState = PagingUiState(),
) {
    val isSuccess
        get() = pagingInfo.isSuccess

    val isError
        get() = pagingInfo.isError

    val isLoading
        get() = pagingInfo.isLoading
}
