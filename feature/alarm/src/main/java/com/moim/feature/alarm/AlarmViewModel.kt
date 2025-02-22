package com.moim.feature.alarm

import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.data.datasource.notification.NotificationRepository
import com.moim.core.model.Alarm
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : BaseViewModel() {

    init {
        setUiState(AlarmUiState.Success())
    }

    fun onUiAction(uiAction: AlarmUiAction) {
        when (uiAction) {
            is AlarmUiAction.OnClickBack -> setUiEvent(AlarmUiEvent.NavigateToBack)
            is AlarmUiAction.OnClickRefresh -> onRefresh()
            is AlarmUiAction.OnClickAlarm -> {}
        }
    }
}

sealed interface AlarmUiState : UiState {
    data object Loading : AlarmUiState

    data class Success(val alarms: List<Alarm> = emptyList()) : AlarmUiState

    data object Error : AlarmUiState
}

sealed interface AlarmUiAction : UiAction {
    data object OnClickBack : AlarmUiAction

    data object OnClickRefresh : AlarmUiAction

    data class OnClickAlarm(val id: String) : AlarmUiAction
}

sealed interface AlarmUiEvent : UiEvent {
    data object NavigateToBack : AlarmUiEvent

    data class NavigateToDeepLink(val deepLink: String) : AlarmUiEvent
}