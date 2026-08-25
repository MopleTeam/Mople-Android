package com.moim.feature.alarm.model

import androidx.compose.runtime.Immutable
import com.moim.core.ui.view.PagingUiState

@Immutable
data class AlarmState(
    val alarms: List<AlarmUiModel> = emptyList(),
    val pagingInfo: PagingUiState = PagingUiState(),
) {
    val isSuccess
        get() = pagingInfo.isSuccess

    val isError
        get() = pagingInfo.isError

    val isLoading
        get() = pagingInfo.isLoading
}
