package com.moim.feature.userwithdrawalforleaderchange.model

import androidx.compose.runtime.Immutable
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.User
import com.moim.core.ui.view.PagingUiState

@Immutable
data class UserWithdrawalForLeaderChangeState(
    val user: User = User(""),
    val meetings: List<Meeting> = emptyList(),
    val isShowExitDialog: Boolean = false,
    val pagingInfo: PagingUiState = PagingUiState(),
) {
    val isSuccess
        get() = pagingInfo.isSuccess

    val isError
        get() = pagingInfo.isError

    val isLoading
        get() = pagingInfo.isLoading
}
