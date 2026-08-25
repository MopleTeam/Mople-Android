package com.moim.feature.meetingnotice.model

import androidx.compose.runtime.Immutable
import com.moim.core.common.model.User
import com.moim.core.ui.view.PagingUiState

@Immutable
data class MeetingNoticeState(
    val user: User = User(userId = ""),
    val isHostUser: Boolean = false,
    val selectedTabIndex: Int = 0,
    val tabStates: Map<Int, NoticeTabState> = emptyMap(),
) {
    val currentTab: NoticeTabState
        get() = tabStates[selectedTabIndex] ?: NoticeTabState()

    val isSuccess
        get() = currentTab.pagingInfo.isSuccess

    val isError
        get() = currentTab.pagingInfo.isError

    val isLoading
        get() = currentTab.pagingInfo.isLoading
}

@Immutable
data class NoticeTabState(
    val notices: List<NoticeUiModel> = emptyList(),
    val pagingInfo: PagingUiState = PagingUiState(),
    val isLoaded: Boolean = false,
)
