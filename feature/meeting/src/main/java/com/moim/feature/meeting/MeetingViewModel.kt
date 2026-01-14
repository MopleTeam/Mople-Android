package com.moim.feature.meeting

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import com.moim.core.common.model.Meeting
import com.moim.core.domain.usecase.GetMeetingsUseCase
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.eventbus.actionStateIn
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.checkedActionedAtIsBeforeLoadedAt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@HiltViewModel
class MeetingViewModel @Inject constructor(
    meetingEventBus: EventBus<MeetingAction>,
    planEventBus: EventBus<PlanAction>,
    private val getMeetingsUseCase: GetMeetingsUseCase,
) : BaseViewModel() {
    private val meetingActionReceiver =
        meetingEventBus
            .action
            .actionStateIn(viewModelScope, MeetingAction.None)
    private val planActionReceiver =
        planEventBus
            .action
            .actionStateIn(viewModelScope, PlanAction.None)

    private var _meetings = getMeetingsUseCase().cachedIn(viewModelScope)
    val meetings =
        merge(
            meetingActionReceiver.flatMapLatest { receiver ->
                when (receiver) {
                    is MeetingAction.None -> {
                        _meetings
                    }

                    is MeetingAction.MeetingCreate -> {
                        _meetings.map { pagingData ->
                            pagingData.checkedActionedAtIsBeforeLoadedAt(
                                actionedAt = receiver.actionAt,
                                loadedAt = getMeetingsUseCase.loadedAt,
                            ) {
                                pagingData.insertSeparators { before: Meeting?, after: Meeting? ->
                                    if (before == null) {
                                        return@insertSeparators receiver.meeting
                                    } else {
                                        null
                                    }
                                }
                            }
                        }
                    }

                    is MeetingAction.MeetingUpdate -> {
                        _meetings.map { pagingData ->
                            pagingData.checkedActionedAtIsBeforeLoadedAt(
                                actionedAt = receiver.actionAt,
                                loadedAt = getMeetingsUseCase.loadedAt,
                            ) {
                                pagingData.map { meeting ->
                                    if (meeting.id == receiver.meeting.id) {
                                        receiver.meeting
                                    } else {
                                        meeting
                                    }
                                }
                            }
                        }
                    }

                    is MeetingAction.MeetingDelete -> {
                        _meetings.map { pagingData ->
                            pagingData.checkedActionedAtIsBeforeLoadedAt(
                                actionedAt = receiver.actionAt,
                                loadedAt = getMeetingsUseCase.loadedAt,
                            ) {
                                pagingData
                                    .map { meeting ->
                                        if (meeting.id == receiver.meetId) {
                                            meeting.apply { isDeleted = true }
                                        } else {
                                            meeting
                                        }
                                    }.filter { it.isDeleted.not() }
                            }
                        }
                    }

                    is MeetingAction.MeetingInvalidate -> {
                        setUiEvent(MeetingUiEvent.RefreshPagingData)
                        _meetings
                    }
                }.also { _meetings = it }
            },
            planActionReceiver.flatMapLatest { receiver ->
                when (receiver) {
                    is PlanAction.None -> {
                        _meetings
                    }

                    is PlanAction.PlanCreate -> {
                        _meetings.map { pagingData ->
                            pagingData.checkedActionedAtIsBeforeLoadedAt(
                                actionedAt = receiver.actionAt,
                                loadedAt = getMeetingsUseCase.loadedAt,
                            ) {
                                pagingData.map { meeting ->
                                    if (meeting.id == receiver.planItem.meetingId) {
                                        val currentLastAt = meeting.lastPlanAt
                                        val newLastAt = receiver.planItem.planAt
                                        val lastPlanAt = listOfNotNull(currentLastAt, newLastAt).minOrNull()
                                        meeting.copy(lastPlanAt = lastPlanAt ?: newLastAt)
                                    } else {
                                        meeting
                                    }
                                }
                            }
                        }
                    }

                    is PlanAction.PlanUpdate -> {
                        _meetings.map { pagingData ->
                            pagingData.checkedActionedAtIsBeforeLoadedAt(
                                actionedAt = receiver.actionAt,
                                loadedAt = getMeetingsUseCase.loadedAt,
                            ) {
                                pagingData.map { meeting ->
                                    if (meeting.id == receiver.planItem.meetingId) {
                                        val currentLastAt = meeting.lastPlanAt
                                        val newLastAt = receiver.planItem.planAt
                                        val lastPlanAt = listOfNotNull(currentLastAt, newLastAt).minOrNull()
                                        meeting.copy(lastPlanAt = lastPlanAt ?: newLastAt)
                                    } else {
                                        meeting
                                    }
                                }
                            }
                        }
                    }

                    is PlanAction.PlanInvalidate,
                    is PlanAction.PlanDelete,
                    -> {
                        setUiEvent(MeetingUiEvent.RefreshPagingData)
                        _meetings
                    }
                }.also { _meetings = it }
            },
        ).cachedIn(viewModelScope)

    fun onUiAction(uiAction: MeetingUiAction) {
        when (uiAction) {
            is MeetingUiAction.OnClickMeetingWrite -> setUiEvent(MeetingUiEvent.NavigateToMeetingWrite)
            is MeetingUiAction.OnClickMeeting -> setUiEvent(MeetingUiEvent.NavigateToMeetingDetail(uiAction.meetingId))
            is MeetingUiAction.OnClickRefresh -> setUiEvent(MeetingUiEvent.RefreshPagingData)
        }
    }
}

sealed interface MeetingUiAction : UiAction {
    data class OnClickMeeting(
        val meetingId: String,
    ) : MeetingUiAction

    data object OnClickMeetingWrite : MeetingUiAction

    data object OnClickRefresh : MeetingUiAction
}

sealed interface MeetingUiEvent : UiEvent {
    data object NavigateToMeetingWrite : MeetingUiEvent

    data class NavigateToMeetingDetail(
        val meetingId: String,
    ) : MeetingUiEvent

    data object RefreshPagingData : MeetingUiEvent
}
