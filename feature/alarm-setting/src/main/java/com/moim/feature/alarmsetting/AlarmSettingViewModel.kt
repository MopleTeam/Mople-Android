package com.moim.feature.alarmsetting

import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.ToastMessage
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.data.datasource.notification.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class AlarmSettingViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : BaseViewModel() {

    private val alarmSettingResult = loadDataSignal
        .flatMapLatest { notificationRepository.getNotificationSubscribes().asResult() }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            alarmSettingResult.collect { result ->
                when (result) {
                    is Result.Loading -> setUiState(AlarmSettingUiState.Loading)
                    is Result.Success -> setUiState(
                        AlarmSettingUiState.Success(
                            isSubscribeForMeetingNotify = result.data.any { it == ENABLE_MEET },
                            isSubscribeForPlanNotify = result.data.any { it == ENABLE_PLAN },
                        )
                    )

                    is Result.Error -> setUiState(AlarmSettingUiState.Error)
                }
            }
        }
    }

    fun onUiAction(uiAction: AlarmSettingUiAction) {
        when (uiAction) {
            is AlarmSettingUiAction.OnClickBack -> setUiEvent(AlarmSettingUiEvent.NavigateToBack)
            is AlarmSettingUiAction.OnClickRefresh -> onRefresh()
            is AlarmSettingUiAction.OnClickPermissionRequest -> setUiEvent(AlarmSettingUiEvent.NavigateToSystemSetting)
            is AlarmSettingUiAction.OnChangeMeetingNotify -> setSubscribeNotify(ENABLE_MEET, uiAction.isCheck)
            is AlarmSettingUiAction.OnChangePlanNotify -> setSubscribeNotify(ENABLE_PLAN, uiAction.isCheck)
        }
    }

    private fun setSubscribeNotify(topic: String, isCheck: Boolean) {
        viewModelScope.launch {
            if (isCheck) {
                notificationRepository.setNotificationSubscribe(topic)
            } else {
                notificationRepository.setNotificationUnSubscribe(topic)
            }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                uiState.checkState<AlarmSettingUiState.Success> {
                    when (result) {
                        is Result.Loading -> return@collect

                        is Result.Success -> {
                            val isMeetTopic = (topic == ENABLE_MEET)

                            setUiState(
                                copy(
                                    isSubscribeForMeetingNotify = if (isMeetTopic) isCheck else isSubscribeForMeetingNotify,
                                    isSubscribeForPlanNotify = if (!isMeetTopic) isCheck else isSubscribeForPlanNotify,
                                )
                            )
                        }

                        is Result.Error -> when (result.exception) {
                            is IOException -> setUiEvent(AlarmSettingUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                            else -> setUiEvent(AlarmSettingUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val ENABLE_MEET = "meet"
        private const val ENABLE_PLAN = "plan"
    }
}

sealed interface AlarmSettingUiState : UiState {
    data object Loading : AlarmSettingUiState

    data class Success(
        val isSubscribeForMeetingNotify: Boolean,
        val isSubscribeForPlanNotify: Boolean,
    ) : AlarmSettingUiState

    data object Error : AlarmSettingUiState
}

sealed interface AlarmSettingUiAction : UiAction {
    data object OnClickBack : AlarmSettingUiAction
    data object OnClickRefresh : AlarmSettingUiAction
    data object OnClickPermissionRequest : AlarmSettingUiAction
    data class OnChangeMeetingNotify(val isCheck: Boolean) : AlarmSettingUiAction
    data class OnChangePlanNotify(val isCheck: Boolean) : AlarmSettingUiAction
}

sealed interface AlarmSettingUiEvent : UiEvent {
    data object NavigateToBack : AlarmSettingUiEvent
    data object NavigateToSystemSetting : AlarmSettingUiEvent
    data class ShowToastMessage(val message: ToastMessage) : AlarmSettingUiEvent
}