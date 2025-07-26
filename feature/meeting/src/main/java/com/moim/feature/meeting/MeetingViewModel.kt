package com.moim.feature.meeting

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
import androidx.paging.map
import com.moim.core.common.delegate.MeetingAction
import com.moim.core.common.delegate.MeetingViewModelDelegate
import com.moim.core.common.delegate.PlanAction
import com.moim.core.common.delegate.PlanItemViewModelDelegate
import com.moim.core.common.delegate.meetingStateIn
import com.moim.core.common.delegate.planItemStateIn
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.checkedActionedAtIsBeforeLoadedAt
import com.moim.core.domain.usecase.GetMeetingsUseCase
import com.moim.core.model.Meeting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.retry
import javax.inject.Inject

@HiltViewModel
class MeetingViewModel @Inject constructor(
    private val getMeetingsUseCase: GetMeetingsUseCase,
    private val meetingViewModelDelegate: MeetingViewModelDelegate,
    private val planItemViewModelDelegate: PlanItemViewModelDelegate
) : BaseViewModel(),
    MeetingViewModelDelegate by meetingViewModelDelegate,
    PlanItemViewModelDelegate by planItemViewModelDelegate {

    private val meetingActionReceiver = meetingAction.meetingStateIn(viewModelScope)
    private val planActionReceiver = planItemAction.planItemStateIn(viewModelScope)

    private var _meetings = getMeetingsUseCase().cachedIn(viewModelScope)
    val meetings = merge(
        meetingActionReceiver.flatMapLatest { receiver ->
            when (receiver) {
                is MeetingAction.None -> _meetings

                is MeetingAction.MeetingCreate -> {
                    _meetings.map { pagingData ->
                        pagingData.checkedActionedAtIsBeforeLoadedAt(
                            actionedAt = receiver.actionAt,
                            loadedAt = getMeetingsUseCase.loadedAt
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
                            loadedAt = getMeetingsUseCase.loadedAt
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
                            loadedAt = getMeetingsUseCase.loadedAt
                        ) {
                            pagingData
                                .map { meeting ->
                                    if (meeting.id == receiver.meetId) {
                                        meeting.apply { isDeleted = true }
                                    } else {
                                        meeting
                                    }
                                }
                                .filter { it.isDeleted.not() }
                        }
                    }
                }

                is MeetingAction.MeetingInvalidate -> {
                    setUiEvent(MeetingUiEvent.RefreshPagingData)
                    _meetings
                }
            }.also {
                _meetings = it
            }
        },
        planActionReceiver.flatMapLatest { receiver ->
            when (receiver) {
                is PlanAction.None -> _meetings

                is PlanAction.PlanCreate -> {
                    _meetings.map { pagingData ->
                        pagingData.checkedActionedAtIsBeforeLoadedAt(
                            actionedAt = receiver.actionAt,
                            loadedAt = getMeetingsUseCase.loadedAt
                        ) {
                            pagingData.map { meeting ->
                                if (meeting.id == receiver.planItem.meetingId) {
                                    val currentLastAt = meeting.lastPlanAt
                                    val newLastAt = receiver.planItem.planAt

                                    if (newLastAt.isBefore(currentLastAt)) {
                                        meeting
                                    } else {
                                        meeting.copy(lastPlanAt = newLastAt)
                                    }
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
                            loadedAt = getMeetingsUseCase.loadedAt
                        ) {
                            pagingData.map { meeting ->
                                if (meeting.id == receiver.planItem.meetingId) {
                                    val currentLastAt = meeting.lastPlanAt
                                    val newLastAt = receiver.planItem.planAt

                                    if (newLastAt.isBefore(currentLastAt)) {
                                        meeting
                                    } else {
                                        meeting.copy(lastPlanAt = newLastAt)
                                    }
                                } else {
                                    meeting
                                }
                            }
                        }
                    }
                }

                is PlanAction.PlanInvalidate,
                is PlanAction.PlanDelete -> {
                    setUiEvent(MeetingUiEvent.RefreshPagingData)
                    _meetings
                }
            }.also { _meetings = it }
        }
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
    data class OnClickMeeting(val meetingId: String) : MeetingUiAction
    data object OnClickMeetingWrite : MeetingUiAction
    data object OnClickRefresh : MeetingUiAction
}

sealed interface MeetingUiEvent : UiEvent {
    data object NavigateToMeetingWrite : MeetingUiEvent
    data class NavigateToMeetingDetail(val meetingId: String) : MeetingUiEvent
    data object RefreshPagingData : MeetingUiEvent
}