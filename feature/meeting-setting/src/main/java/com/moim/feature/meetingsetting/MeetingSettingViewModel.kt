package com.moim.feature.meetingsetting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.moim.core.common.delegate.MeetingAction
import com.moim.core.common.delegate.MeetingViewModelDelegate
import com.moim.core.common.delegate.PlanViewModelDelegate
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.ToastMessage
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.model.Meeting
import com.moim.core.route.DetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MeetingSettingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val meetingRepository: MeetingRepository,
    userRepository: UserRepository,
    meetingViewModelDelegate: MeetingViewModelDelegate,
    planViewModelDelegate: PlanViewModelDelegate
) : BaseViewModel(),
    MeetingViewModelDelegate by meetingViewModelDelegate,
    PlanViewModelDelegate by planViewModelDelegate {

    private val meeting
        get() = savedStateHandle
            .toRoute<DetailRoute.MeetingSetting>(DetailRoute.MeetingSetting.typeMap)
            .meeting

    private val meetingActionReceiver = meetingAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MeetingAction.None)

    init {
        viewModelScope.launch {
            launch {
                val user = userRepository.getUser().first()

                setUiState(
                    MeetingSettingUiState.MeetingSetting(
                        meeting = meeting,
                        isHostUser = meeting.creatorId == user.userId
                    )
                )
            }

            launch {
                meetingActionReceiver.collect { action ->
                    uiState.checkState<MeetingSettingUiState.MeetingSetting> {
                        if (action is MeetingAction.MeetingUpdate) {
                            setUiState(copy(meeting = action.meeting))
                        }
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: MeetingSettingUiAction) {
        when (uiAction) {
            is MeetingSettingUiAction.OnClickBack -> setUiEvent(MeetingSettingUiEvent.NavigateToBack)
            is MeetingSettingUiAction.OnClickMeetingEdit -> setUiEvent(MeetingSettingUiEvent.NavigateToMeetingWrite(uiAction.meeting))
            is MeetingSettingUiAction.OnClickMeetingExit -> deleteMeeting()
            is MeetingSettingUiAction.OnClickMeetingParticipants -> {}
            is MeetingSettingUiAction.OnClickAddUser -> {}
            is MeetingSettingUiAction.OnShowMeetingExitDialog -> showMeetingExitDialog(uiAction.isShow)
            is MeetingSettingUiAction.OnShowMeetingDeleteDialog -> showMeetingDeleteDialog(uiAction.isShow)
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
                meetingRepository.deleteMeeting(meeting.id)
                    .asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> {
                                deleteMeeting(ZonedDateTime.now(), meeting.id)
                                invalidatePlan(ZonedDateTime.now())
                                setUiEvent(MeetingSettingUiEvent.NavigateToBackForDelete)
                            }

                            is Result.Error -> when (result.exception) {
                                is IOException -> setUiEvent(MeetingSettingUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                                is NetworkException -> setUiEvent(MeetingSettingUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                            }
                        }
                    }
            }
        }
    }
}

sealed interface MeetingSettingUiState : UiState {
    data class MeetingSetting(
        val meeting: Meeting,
        val isHostUser: Boolean = false,
        val isShowMeetingDeleteDialog: Boolean = false,
        val isShowMeetingExitDialog: Boolean = false
    ) : MeetingSettingUiState
}

sealed interface MeetingSettingUiAction : UiAction {
    data object OnClickBack : MeetingSettingUiAction
    data object OnClickAddUser : MeetingSettingUiAction
    data object OnClickMeetingExit : MeetingSettingUiAction
    data class OnClickMeetingParticipants(val meetingId: String) : MeetingSettingUiAction
    data class OnClickMeetingEdit(val meeting: Meeting) : MeetingSettingUiAction
    data class OnShowMeetingExitDialog(val isShow: Boolean) : MeetingSettingUiAction
    data class OnShowMeetingDeleteDialog(val isShow: Boolean) : MeetingSettingUiAction
}

sealed interface MeetingSettingUiEvent : UiEvent {
    data object NavigateToBack : MeetingSettingUiEvent
    data object NavigateToBackForDelete : MeetingSettingUiEvent
    data class NavigateToMeetingWrite(val meeting: Meeting) : MeetingSettingUiEvent
    data class NavigateToMeetingParticipants(val meetingId: String) : MeetingSettingUiEvent
    data class ShowToastMessage(val message: ToastMessage) : MeetingSettingUiEvent
}