package com.moim.feature.meetingsetting

import androidx.lifecycle.viewModelScope
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.eventbus.actionStateIn
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException

@HiltViewModel(assistedFactory = MeetingSettingViewModel.Factory::class)
class MeetingSettingViewModel @AssistedInject constructor(
    userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
    private val meetingEventBus: EventBus<MeetingAction>,
    private val planEventBus: EventBus<PlanAction>,
    @Assisted val meetingSettingRoute: DetailRoute.MeetingSetting,
) : BaseViewModel() {
    private val meeting = meetingSettingRoute.meeting

    private val meetingActionReceiver =
        meetingEventBus
            .action
            .actionStateIn(viewModelScope, MeetingAction.None)

    init {
        viewModelScope.launch {
            launch {
                val user = userRepository.getUser().first()

                setUiState(
                    MeetingSettingUiState.MeetingSetting(
                        meeting = meeting,
                        isHostUser = meeting.creatorId == user.userId,
                    ),
                )
            }

            launch {
                meetingActionReceiver
                    .filterIsInstance<MeetingAction.MeetingUpdate>()
                    .collect { action ->
                        uiState.checkState<MeetingSettingUiState.MeetingSetting> {
                            setUiState(copy(meeting = action.meeting))
                        }
                    }
            }
        }
    }

    fun onUiAction(uiAction: MeetingSettingUiAction) {
        when (uiAction) {
            is MeetingSettingUiAction.OnClickBack -> {
                setUiEvent(MeetingSettingUiEvent.NavigateToBack)
            }

            is MeetingSettingUiAction.OnClickMeetingEdit -> {
                setUiEvent(MeetingSettingUiEvent.NavigateToMeetingWrite(uiAction.meeting))
            }

            is MeetingSettingUiAction.OnClickMeetingExit -> {
                deleteMeeting()
            }

            is MeetingSettingUiAction.OnClickMeetingParticipants -> {
                setUiEvent(
                    MeetingSettingUiEvent.NavigateToMeetingParticipants(uiAction.viewIdType),
                )
            }

            is MeetingSettingUiAction.OnShowMeetingExitDialog -> {
                showMeetingExitDialog(uiAction.isShow)
            }

            is MeetingSettingUiAction.OnShowMeetingDeleteDialog -> {
                showMeetingDeleteDialog(uiAction.isShow)
            }
        }
    }

    private fun showMeetingExitDialog(isShow: Boolean) {
        uiState.checkState<MeetingSettingUiState.MeetingSetting> {
            setUiState(copy(isShowMeetingExitDialog = isShow))
        }
    }

    private fun showMeetingDeleteDialog(isShow: Boolean) {
        uiState.checkState<MeetingSettingUiState.MeetingSetting> {
            setUiState(copy(isShowMeetingDeleteDialog = isShow))
        }
    }

    private fun deleteMeeting() {
        viewModelScope.launch {
            uiState.checkState<MeetingSettingUiState.MeetingSetting> {
                meetingRepository
                    .deleteMeeting(meeting.id)
                    .asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> {
                                return@collect
                            }

                            is Result.Success -> {
                                meetingEventBus.send(MeetingAction.MeetingDelete(meetId = meeting.id))
                                planEventBus.send(PlanAction.PlanInvalidate())
                                setUiEvent(MeetingSettingUiEvent.NavigateToBackForDelete)
                            }

                            is Result.Error -> {
                                showErrorMessage(result.exception)
                            }
                        }
                    }
            }
        }
    }

    private fun showErrorMessage(error: Throwable) {
        when (error) {
            is IOException -> setUiEvent(MeetingSettingUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
            is NetworkException -> setUiEvent(MeetingSettingUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(meetingSettingRoute: DetailRoute.MeetingSetting): MeetingSettingViewModel
    }
}

sealed interface MeetingSettingUiState : UiState {
    data class MeetingSetting(
        val meeting: Meeting,
        val isHostUser: Boolean = false,
        val isShowMeetingDeleteDialog: Boolean = false,
        val isShowMeetingExitDialog: Boolean = false,
    ) : MeetingSettingUiState
}

sealed interface MeetingSettingUiAction : UiAction {
    data object OnClickBack : MeetingSettingUiAction

    data object OnClickMeetingExit : MeetingSettingUiAction

    data class OnClickMeetingParticipants(
        val viewIdType: ViewIdType,
    ) : MeetingSettingUiAction

    data class OnClickMeetingEdit(
        val meeting: Meeting,
    ) : MeetingSettingUiAction

    data class OnShowMeetingExitDialog(
        val isShow: Boolean,
    ) : MeetingSettingUiAction

    data class OnShowMeetingDeleteDialog(
        val isShow: Boolean,
    ) : MeetingSettingUiAction
}

sealed interface MeetingSettingUiEvent : UiEvent {
    data object NavigateToBack : MeetingSettingUiEvent

    data object NavigateToBackForDelete : MeetingSettingUiEvent

    data class NavigateToMeetingWrite(
        val meeting: Meeting,
    ) : MeetingSettingUiEvent

    data class NavigateToMeetingParticipants(
        val viewIdType: ViewIdType,
    ) : MeetingSettingUiEvent

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : MeetingSettingUiEvent
}
