package com.moim.feature.meeting

import androidx.lifecycle.viewModelScope
import com.moim.core.common.delegate.MeetingAction
import com.moim.core.common.delegate.MeetingViewModelDelegate
import com.moim.core.common.delegate.PlanAction
import com.moim.core.common.delegate.PlanItemViewModelDelegate
import com.moim.core.common.delegate.meetingStateIn
import com.moim.core.common.delegate.planItemStateIn
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.common.view.restartableStateIn
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.model.Meeting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetingViewModel @Inject constructor(
    meetingRepository: MeetingRepository,
    private val meetingViewModelDelegate: MeetingViewModelDelegate,
    private val planItemViewModelDelegate: PlanItemViewModelDelegate
) : BaseViewModel(),
    MeetingViewModelDelegate by meetingViewModelDelegate,
    PlanItemViewModelDelegate by planItemViewModelDelegate {

    private val meetingActionReceiver = meetingAction.meetingStateIn(viewModelScope)
    private val planActionReceiver = planItemAction.planItemStateIn(viewModelScope)

    private val meetingsResult = meetingRepository.getMeetings()
        .asResult()
        .restartableStateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            launch {
                meetingsResult.collect { result ->
                    when (result) {
                        is Result.Loading -> setUiState(MeetingUiState.Loading)
                        is Result.Success -> setUiState(MeetingUiState.Success(result.data))
                        is Result.Error -> setUiState(MeetingUiState.Error)
                    }
                }
            }

            launch {
                meetingActionReceiver.collect { action ->
                    uiState.checkState<MeetingUiState.Success> {
                        when (action) {
                            is MeetingAction.MeetingCreate -> {
                                val meeting = meetings.toMutableList().apply { add(0, action.meeting) }
                                setUiState(copy(meetings = meeting))
                            }

                            is MeetingAction.MeetingUpdate -> {
                                val meeting = meetings.toMutableList().apply {
                                    withIndex()
                                        .firstOrNull { action.meeting.id == it.value.id }
                                        ?.index
                                        ?.let { index -> set(index, action.meeting) }
                                }

                                setUiState(copy(meetings = meeting))
                            }

                            is MeetingAction.MeetingDelete -> {
                                val meeting = meetings.toMutableList().apply {
                                    withIndex()
                                        .firstOrNull { action.meetId == it.value.id }
                                        ?.index
                                        ?.let { index -> removeAt(index) }
                                }

                                setUiState(copy(meetings = meeting))
                            }

                            is MeetingAction.MeetingInvalidate -> meetingsResult.restart()

                            else -> return@collect
                        }
                    }
                }
            }

            launch {
                planActionReceiver.collect { action ->
                    uiState.checkState<MeetingUiState.Success> {
                        when (action) {
                            is PlanAction.PlanCreate -> {
                                val meeting = meetings.withIndex().find { it.value.id == action.planItem.meetingId } ?: return@collect
                                val currentLastAt = meeting.value.lastPlanAt
                                val newLastAt = action.planItem.planAt

                                if (newLastAt.isBefore(currentLastAt)) return@collect

                                setUiState(
                                    copy(
                                        meetings = meetings
                                            .toMutableList()
                                            .apply { set(meeting.index, meeting.value.copy(lastPlanAt = action.planItem.planAt)) }
                                    )
                                )
                            }

                            is PlanAction.PlanUpdate -> {
                                val meeting = meetings.withIndex().find { it.value.id == action.planItem.meetingId } ?: return@collect
                                val currentLastAt = meeting.value.lastPlanAt
                                val newLastAt = action.planItem.planAt

                                if (newLastAt.isBefore(currentLastAt)) return@collect
                                setUiState(
                                    copy(
                                        meetings = meetings
                                            .toMutableList()
                                            .apply { set(meeting.index, meeting.value.copy(lastPlanAt = action.planItem.planAt)) }
                                    )
                                )
                            }

                            is PlanAction.PlanDelete, is PlanAction.PlanInvalidate -> meetingsResult.restart()
                            is PlanAction.None -> return@collect
                        }
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: MeetingUiAction) {
        when (uiAction) {
            is MeetingUiAction.OnClickMeetingWrite -> setUiEvent(MeetingUiEvent.NavigateToMeetingWrite)
            is MeetingUiAction.OnClickMeeting -> setUiEvent(MeetingUiEvent.NavigateToMeetingDetail(uiAction.meetingId))
            is MeetingUiAction.OnClickRefresh -> meetingsResult.restart()
        }
    }
}

sealed interface MeetingUiState : UiState {
    data object Loading : MeetingUiState
    data class Success(val meetings: List<Meeting>) : MeetingUiState
    data object Error : MeetingUiState
}

sealed interface MeetingUiAction : UiAction {
    data class OnClickMeeting(val meetingId: String) : MeetingUiAction
    data object OnClickMeetingWrite : MeetingUiAction
    data object OnClickRefresh : MeetingUiAction
}

sealed interface MeetingUiEvent : UiEvent {
    data object NavigateToMeetingWrite : MeetingUiEvent
    data class NavigateToMeetingDetail(val meetingId: String) : MeetingUiEvent
}