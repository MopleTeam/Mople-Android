package com.moim.feature.meeting

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.PagingUiState
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import com.moim.feature.meeting.model.MeetingUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MeetingViewModel @Inject constructor(
    meetingEventBus: EventBus<MeetingAction>,
    planEventBus: EventBus<PlanAction>,
    userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
) : BaseViewModel() {
    private var pagingJob: Job? = null

    init {
        viewModelScope.launch {
            launch {
                val user = userRepository.getUser().first()
                setUiState(MeetingUiState(user = user))
                getMeetings()
            }

            launch {
                planEventBus.action.collect { action ->
                    uiState.checkState<MeetingUiState> {
                        when (action) {
                            is PlanAction.None -> {
                                return@collect
                            }

                            is PlanAction.PlanCreate -> {
                                val meetings =
                                    meetings.map { uiModel ->
                                        if (uiModel.meeting.id == action.planItem.meetingId) {
                                            uiModel.copy(
                                                meeting =
                                                    updateLastPlanAt(
                                                        meeting = uiModel.meeting,
                                                        planAt = action.planItem.planAt,
                                                    ),
                                            )
                                        } else {
                                            uiModel
                                        }
                                    }

                                setUiState(copy(meetings = meetings))
                            }

                            is PlanAction.PlanUpdate -> {
                                val meetings =
                                    meetings.map { uiModel ->
                                        if (uiModel.meeting.id == action.planItem.meetingId) {
                                            uiModel.copy(
                                                meeting =
                                                    updateLastPlanAt(
                                                        meeting = uiModel.meeting,
                                                        planAt = action.planItem.planAt,
                                                    ),
                                            )
                                        } else {
                                            uiModel
                                        }
                                    }

                                setUiState(copy(meetings = meetings))
                            }

                            is PlanAction.PlanDelete,
                            is PlanAction.PlanInvalidate,
                            -> {
                                getMeetings()
                            }
                        }
                    }
                }
            }

            launch {
                meetingEventBus.action.collect { action ->
                    uiState.checkState<MeetingUiState> {
                        when (action) {
                            is MeetingAction.None -> {
                                return@collect
                            }

                            is MeetingAction.MeetingCreate -> {
                                val isLeader = user.userId == action.meeting.creatorId
                                val meetings =
                                    meetings
                                        .toMutableList()
                                        .apply {
                                            add(
                                                MeetingUiModel(
                                                    meeting = action.meeting,
                                                    isLeader = isLeader,
                                                ),
                                            )
                                        }

                                setUiState(copy(meetings = meetings))
                            }

                            is MeetingAction.MeetingUpdate -> {
                                val isLeader = user.userId == action.meeting.creatorId
                                val meetings =
                                    meetings.map { uiModel ->
                                        if (uiModel.meeting.id == action.meeting.id) {
                                            uiModel.copy(
                                                meeting = action.meeting,
                                                isLeader = isLeader,
                                            )
                                        } else {
                                            uiModel
                                        }
                                    }

                                setUiState(copy(meetings = meetings))
                            }

                            is MeetingAction.MeetingDelete -> {
                                val meetings =
                                    meetings
                                        .toMutableList()
                                        .apply { removeIf { uiModel -> uiModel.meeting.id == action.meetId } }

                                setUiState(copy(meetings = meetings))
                            }

                            is MeetingAction.MeetingInvalidate -> {
                                getMeetings()
                            }
                        }
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: MeetingUiAction) {
        when (uiAction) {
            is MeetingUiAction.OnClickMeeting -> {
                setUiEvent(MeetingUiEvent.NavigateToMeetingDetail(uiAction.meetingId))
            }

            is MeetingUiAction.OnClickMeetingWrite -> {
                setUiEvent(MeetingUiEvent.NavigateToMeetingWrite)
            }

            is MeetingUiAction.OnClickRefresh -> {
                val uiState = uiState.value as? MeetingUiState ?: return
                getMeetings(uiState.pagingInfo.nextCursor)
            }

            is MeetingUiAction.OnNextPageLoad -> {
                val uiState = uiState.value as? MeetingUiState ?: return
                getMeetings(uiState.pagingInfo.nextCursor)
            }
        }
    }

    private fun getMeetings(cursor: String? = null) {
        if (pagingJob.isActiveCheck()) return
        pagingJob =
            viewModelScope.launch {
                handlePagingData(
                    pagingInfo = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingInfo =
                    runCatching {
                        meetingRepository.getMeetings(
                            cursor = cursor ?: "",
                            size = 30,
                        )
                    }.getOrNull()

                handlePagingData(
                    pagingInfo = pagingInfo,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private fun handlePagingData(
        pagingInfo: PaginationContainer<List<Meeting>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        if (cursor == null) {
            initializePagingData(
                pagingData = pagingInfo,
                isLoading = isLoading,
            )
        }

        if (cursor != null) {
            addLoadPagingData(
                pagingData = pagingInfo,
                isLoading = isLoading,
            )
        }
    }

    private fun initializePagingData(
        pagingData: PaginationContainer<List<Meeting>>?,
        isLoading: Boolean,
    ) {
        uiState.checkState<MeetingUiState> {
            val uiState =
                when {
                    isLoading -> {
                        copy(pagingInfo = PagingUiState(isLoading = true))
                    }

                    pagingData == null -> {
                        copy(
                            pagingInfo =
                                PagingUiState(
                                    isLoading = false,
                                    isError = true,
                                ),
                        )
                    }

                    else -> {
                        val data =
                            pagingData.content.map { meeting ->
                                MeetingUiModel(
                                    meeting = meeting,
                                    isLeader = meeting.creatorId == user.userId,
                                )
                            }

                        copy(
                            pagingInfo =
                                PagingUiState(
                                    isLoading = false,
                                    nextCursor = pagingData.page.nextCursor,
                                    isLast = !pagingData.page.isNext || data.isEmpty(),
                                ),
                            meetings = data,
                        )
                    }
                }

            setUiState(uiState)
        }
    }

    private fun addLoadPagingData(
        pagingData: PaginationContainer<List<Meeting>>?,
        isLoading: Boolean,
    ) {
        uiState.checkState<MeetingUiState> {
            val pagingInfo = this.pagingInfo
            val uiState =
                when {
                    isLoading -> {
                        this.copy(
                            pagingInfo =
                                pagingInfo.copy(
                                    isLoadingFooter = true,
                                    isErrorFooter = false,
                                ),
                            meetings = meetings,
                        )
                    }

                    pagingData == null -> {
                        this.copy(
                            pagingInfo =
                                pagingInfo.copy(
                                    isLoadingFooter = false,
                                    isErrorFooter = true,
                                ),
                            meetings = meetings,
                        )
                    }

                    else -> {
                        val addData =
                            pagingData.content.map { meeting ->
                                MeetingUiModel(
                                    meeting = meeting,
                                    isLeader = meeting.creatorId == user.userId,
                                )
                            }

                        this.copy(
                            pagingInfo =
                                pagingInfo.copy(
                                    isLoadingFooter = false,
                                    isErrorFooter = false,
                                    nextCursor = pagingData.page.nextCursor,
                                    isLast = !pagingData.page.isNext || addData.isEmpty(),
                                ),
                            meetings = meetings.toMutableList().apply { addAll(addData) },
                        )
                    }
                }

            setUiState(uiState)
        }
    }

    private fun updateLastPlanAt(
        meeting: Meeting,
        planAt: ZonedDateTime,
    ): Meeting {
        val currentLastAt = meeting.lastPlanAt
        val newLastAt: ZonedDateTime = planAt
        val lastPlanAt = listOfNotNull(currentLastAt, planAt).minOrNull()
        return meeting.copy(lastPlanAt = lastPlanAt ?: newLastAt)
    }
}

data class MeetingUiState(
    val pagingInfo: PagingUiState = PagingUiState(),
    val user: User,
    val meetings: List<MeetingUiModel> = emptyList(),
) : UiState

sealed interface MeetingUiAction : UiAction {
    data class OnClickMeeting(
        val meetingId: String,
    ) : MeetingUiAction

    data object OnClickMeetingWrite : MeetingUiAction

    data object OnClickRefresh : MeetingUiAction

    data object OnNextPageLoad : MeetingUiAction
}

sealed interface MeetingUiEvent : UiEvent {
    data object NavigateToMeetingWrite : MeetingUiEvent

    data class NavigateToMeetingDetail(
        val meetingId: String,
    ) : MeetingUiEvent
}
