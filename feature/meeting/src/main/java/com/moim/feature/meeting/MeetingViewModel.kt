package com.moim.feature.meeting

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.PaginationContainer
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.PagingHelper
import com.moim.feature.meeting.model.MeetingIntent
import com.moim.feature.meeting.model.MeetingSideEffect
import com.moim.feature.meeting.model.MeetingState
import com.moim.feature.meeting.model.MeetingUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.syntax.Syntax
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MeetingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
    meetingEventBus: EventBus<MeetingAction>,
    planEventBus: EventBus<PlanAction>,
) : MVIViewModel<MeetingState, MeetingSideEffect>(MeetingState()) {
    private var pagingJob: Job? = null

    init {
        planEventBus.action
            .onEach { action ->
                intent {
                    when (action) {
                        is PlanAction.PlanCreate -> {
                            updateLastPlanAt(action.planItem.meetingId, action.planItem.planAt)
                        }

                        is PlanAction.PlanUpdate -> {
                            updateLastPlanAt(action.planItem.meetingId, action.planItem.planAt)
                        }

                        is PlanAction.PlanDelete,
                        is PlanAction.PlanInvalidate,
                        -> {
                            getMeetings()
                        }

                        is PlanAction.None -> {
                            return@intent
                        }
                    }
                }
            }.launchIn(viewModelScope)

        meetingEventBus.action
            .onEach { action ->
                intent {
                    when (action) {
                        is MeetingAction.MeetingCreate -> {
                            val newMeeting =
                                MeetingUiModel(
                                    meeting = action.meeting,
                                    isLeader = state.user.userId == action.meeting.hostId,
                                )

                            reduce { state.copy(meetings = state.meetings + newMeeting) }
                        }

                        is MeetingAction.MeetingUpdate -> {
                            val isLeader = state.user.userId == action.meeting.hostId
                            val meetings =
                                state.meetings.map { uiModel ->
                                    if (uiModel.meeting.id == action.meeting.id) {
                                        uiModel.copy(
                                            meeting = action.meeting,
                                            isLeader = isLeader,
                                        )
                                    } else {
                                        uiModel
                                    }
                                }

                            reduce { state.copy(meetings = meetings) }
                        }

                        is MeetingAction.MeetingDelete -> {
                            val meetings = state.meetings.filterNot { uiModel -> uiModel.meeting.id == action.meetId }

                            reduce { state.copy(meetings = meetings) }
                        }

                        is MeetingAction.MeetingInvalidate -> {
                            getMeetings()
                        }

                        is MeetingAction.None -> {
                            return@intent
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    override suspend fun Syntax<MeetingState, MeetingSideEffect>.onContainerCreate() {
        // isLeader 판별에 필요하므로 목록보다 먼저 채운다.
        val user = userRepository.getUser().first()
        reduce { state.copy(user = user) }

        getMeetings()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is MeetingIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is MeetingIntent.MeetingClick -> {
                    postSideEffect(MeetingSideEffect.NavigateToMeetingDetail(intent.meetingId))
                }

                is MeetingIntent.MeetingWriteClick -> {
                    postSideEffect(MeetingSideEffect.NavigateToMeetingWrite)
                }

                is MeetingIntent.RefreshClick,
                is MeetingIntent.NextPageLoad,
                -> {
                    getMeetings(state.pagingInfo.nextCursor)
                }
            }
        }
    }

    private fun getMeetings(cursor: String? = null) {
        if (pagingJob.isActiveCheck()) return
        pagingJob =
            intent {
                handlePagingData(
                    pagingData = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingData =
                    runCatching {
                        meetingRepository.getMeetings(
                            cursor = cursor ?: "",
                            size = 30,
                        )
                    }.getOrNull()

                handlePagingData(
                    pagingData = pagingData,
                    isLoading = false,
                    cursor = cursor,
                )
            }
    }

    private suspend fun Syntax<MeetingState, MeetingSideEffect>.handlePagingData(
        pagingData: PaginationContainer<List<Meeting>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        val result =
            PagingHelper.handlePagingResult(
                pagingData = pagingData,
                isLoading = isLoading,
                currentPagingInfo = state.pagingInfo,
                currentItems = state.meetings,
                isInitialLoad = cursor == null,
                transform = { meetings ->
                    meetings.map { meeting ->
                        MeetingUiModel(
                            meeting = meeting,
                            isLeader = meeting.hostId == state.user.userId,
                        )
                    }
                },
            )

        reduce {
            state.copy(
                pagingInfo = result.pagingInfo,
                meetings = result.items,
            )
        }
    }

    private suspend fun Syntax<MeetingState, MeetingSideEffect>.updateLastPlanAt(
        meetingId: String,
        planAt: ZonedDateTime,
    ) {
        val meetings =
            state.meetings.map { uiModel ->
                if (uiModel.meeting.id == meetingId) {
                    uiModel.copy(meeting = mergeLastPlanAt(uiModel.meeting, planAt))
                } else {
                    uiModel
                }
            }

        reduce { state.copy(meetings = meetings) }
    }

    private fun mergeLastPlanAt(
        meeting: Meeting,
        planAt: ZonedDateTime,
    ): Meeting {
        val lastPlanAt = listOfNotNull(meeting.lastPlanAt, planAt).minOrNull()
        return meeting.copy(lastPlanAt = lastPlanAt ?: planAt)
    }
}
