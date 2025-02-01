package com.moim.feature.meeting

import androidx.lifecycle.viewModelScope
import com.moim.core.common.delegate.MeetingAction
import com.moim.core.common.delegate.MeetingViewModelDelegate
import com.moim.core.common.delegate.PlanAction
import com.moim.core.common.delegate.PlanViewModelDelegate
import com.moim.core.common.delegate.meetingStateIn
import com.moim.core.common.delegate.planStateIn
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.util.parseZonedDateTimeForDateString
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.model.Meeting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetingViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository,
    private val meetingViewModelDelegate: MeetingViewModelDelegate,
    private val planViewModelDelegate: PlanViewModelDelegate
) : BaseViewModel(),
    MeetingViewModelDelegate by meetingViewModelDelegate,
    PlanViewModelDelegate by planViewModelDelegate {

    private val meetingActionReceiver = meetingAction.meetingStateIn(viewModelScope)
    private val planActionReceiver = planAction.planStateIn(viewModelScope)

    private val meetingsResult = loadDataSignal
        .flatMapLatest { meetingRepository.getMeetings().asResult() }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

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

                            is MeetingAction.MeetingInvalidate -> onRefresh()

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
                                val meeting = meetings.withIndex().find { it.value.id == action.plan.meetingId } ?: return@collect
                                val currentLastAt = meeting.value.lastPlanAt.parseZonedDateTimeForDateString()
                                val newLastAt = action.plan.planTime.parseZonedDateTimeForDateString()

                                if (currentLastAt.isBefore(newLastAt)) return@collect

                                setUiState(
                                    copy(
                                        meetings = meetings
                                            .toMutableList()
                                            .apply { set(meeting.index, meeting.value.copy(lastPlanAt = action.plan.planTime)) }
                                    )
                                )
                            }

                            is PlanAction.PlanUpdate -> {
                                val meeting = meetings.withIndex().find { it.value.id == action.plan.meetingId } ?: return@collect
                                val currentLastAt = meeting.value.lastPlanAt.parseZonedDateTimeForDateString()
                                val newLastAt = action.plan.planTime.parseZonedDateTimeForDateString()

                                if (currentLastAt.isBefore(newLastAt)) return@collect

                                setUiState(
                                    copy(
                                        meetings = meetings
                                            .toMutableList()
                                            .apply { set(meeting.index, meeting.value.copy(lastPlanAt = action.plan.planTime)) }
                                    )
                                )
                            }

                            is PlanAction.PlanDelete, is PlanAction.PlanInvalidate -> onRefresh()
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
            is MeetingUiAction.OnClickRefresh -> onRefresh()
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