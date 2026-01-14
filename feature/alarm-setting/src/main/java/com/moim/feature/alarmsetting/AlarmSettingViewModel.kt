package com.moim.feature.alarmsetting

import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.notification.NotificationRepository
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import com.moim.core.ui.view.restartableStateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class AlarmSettingViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : BaseViewModel() {
    private val alarmSettingResult =
        notificationRepository
            .getNotificationSubscribes()
            .asResult()
            .restartableStateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            alarmSettingResult.collect { result ->
                when (result) {
                    is Result.Loading -> {
                        setUiState(AlarmSettingUiState.Loading)
                    }

                    is Result.Success -> {
                        setUiState(
                            AlarmSettingUiState.Success(
                                isSubscribeForMeetingNotify = result.data.any { it == ENABLE_MEET },
                                isSubscribeForPlanNotify = result.data.any { it == ENABLE_PLAN },
                                isSubscribeForCommentNotify = result.data.any { it == ENABLE_COMMENT },
                                isSubscribeForMentionNotify = result.data.any { it == ENABLE_MENTION },
                            ),
                        )
                    }

                    is Result.Error -> {
                        setUiState(AlarmSettingUiState.Error)
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: AlarmSettingUiAction) {
        when (uiAction) {
            is AlarmSettingUiAction.OnClickBack -> setUiEvent(AlarmSettingUiEvent.NavigateToBack)
            is AlarmSettingUiAction.OnClickRefresh -> alarmSettingResult.restart()
            is AlarmSettingUiAction.OnClickPermissionRequest -> setUiEvent(AlarmSettingUiEvent.NavigateToSystemSetting)
            is AlarmSettingUiAction.OnChangeMeetingNotify -> setSubscribeNotify(ENABLE_MEET, uiAction.isCheck)
            is AlarmSettingUiAction.OnChangePlanNotify -> setSubscribeNotify(ENABLE_PLAN, uiAction.isCheck)
            is AlarmSettingUiAction.OnChangeCommentNotify -> setSubscribeNotify(ENABLE_COMMENT, uiAction.isCheck)
            is AlarmSettingUiAction.OnChangeMentionNotify -> setSubscribeNotify(ENABLE_MENTION, uiAction.isCheck)
        }
    }

    private fun setSubscribeNotify(
        topic: String,
        isCheck: Boolean,
    ) {
        viewModelScope.launch {
            if (isCheck) {
                notificationRepository.setNotificationSubscribe(topic)
            } else {
                notificationRepository.setNotificationUnSubscribe(topic)
            }.asResult().onEach { setLoading(it is Result.Loading) }.collect { result ->
                uiState.checkState<AlarmSettingUiState.Success> {
                    when (result) {
                        is Result.Loading -> {
                            return@collect
                        }

                        is Result.Success -> {
                            setUiState(
                                copy(
                                    isSubscribeForMeetingNotify = if (topic == ENABLE_MEET) isCheck else isSubscribeForMeetingNotify,
                                    isSubscribeForPlanNotify = if (topic == ENABLE_PLAN) isCheck else isSubscribeForPlanNotify,
                                    isSubscribeForCommentNotify = if (topic == ENABLE_COMMENT) isCheck else isSubscribeForCommentNotify,
                                    isSubscribeForMentionNotify = if (topic == ENABLE_MENTION) isCheck else isSubscribeForMentionNotify,
                                ),
                            )
                        }

                        is Result.Error -> {
                            when (result.exception) {
                                is IOException -> setUiEvent(AlarmSettingUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                                else -> setUiEvent(AlarmSettingUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val ENABLE_MEET = "MEET"
        private const val ENABLE_PLAN = "PLAN"
        private const val ENABLE_COMMENT = "REPLY"
        private const val ENABLE_MENTION = "MENTION"
    }
}

sealed interface AlarmSettingUiState : UiState {
    data object Loading : AlarmSettingUiState

    data class Success(
        val isSubscribeForMeetingNotify: Boolean,
        val isSubscribeForPlanNotify: Boolean,
        val isSubscribeForCommentNotify: Boolean,
        val isSubscribeForMentionNotify: Boolean,
    ) : AlarmSettingUiState

    data object Error : AlarmSettingUiState
}

sealed interface AlarmSettingUiAction : UiAction {
    data object OnClickBack : AlarmSettingUiAction

    data object OnClickRefresh : AlarmSettingUiAction

    data object OnClickPermissionRequest : AlarmSettingUiAction

    data class OnChangeMeetingNotify(
        val isCheck: Boolean,
    ) : AlarmSettingUiAction

    data class OnChangePlanNotify(
        val isCheck: Boolean,
    ) : AlarmSettingUiAction

    data class OnChangeCommentNotify(
        val isCheck: Boolean,
    ) : AlarmSettingUiAction

    data class OnChangeMentionNotify(
        val isCheck: Boolean,
    ) : AlarmSettingUiAction
}

sealed interface AlarmSettingUiEvent : UiEvent {
    data object NavigateToBack : AlarmSettingUiEvent

    data object NavigateToSystemSetting : AlarmSettingUiEvent

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : AlarmSettingUiEvent
}
