package com.moim.feature.alarm

import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.data.datasource.notification.NotificationRepository
import com.moim.core.model.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : BaseViewModel() {

    private val notificationResult = loadDataSignal
        .flatMapLatest { notificationRepository.getNotifications().asResult() }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            notificationResult.collect { result ->
                when(result) {
                    is Result.Loading -> setUiState(AlarmUiState.Loading)
                    is Result.Success -> setUiState(AlarmUiState.Success(result.data))
                    is Result.Error -> setUiState(AlarmUiState.Error)
                }
            }
        }
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

    data class Success(val notifications: List<Notification> = emptyList()) : AlarmUiState

    data object Error : AlarmUiState
}

sealed interface AlarmUiAction : UiAction {
    data object OnClickBack : AlarmUiAction

    data object OnClickRefresh : AlarmUiAction

    data class OnClickAlarm(val notification: Notification) : AlarmUiAction
}

sealed interface AlarmUiEvent : UiEvent {
    data object NavigateToBack : AlarmUiEvent

    data class NavigateToDeepLink(val deepLink: String) : AlarmUiEvent
}