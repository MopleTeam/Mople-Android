package com.moim.feature.userwithdrawalforleaderchange

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.PaginationContainer
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.PagingHelper
import com.moim.feature.userwithdrawalforleaderchange.model.UserWithdrawalForLeaderChangeIntent
import com.moim.feature.userwithdrawalforleaderchange.model.UserWithdrawalForLeaderChangeSideEffect
import com.moim.feature.userwithdrawalforleaderchange.model.UserWithdrawalForLeaderChangeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class UserWithdrawalForLeaderChangeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
    meetingEventBus: EventBus<MeetingAction>,
) : MVIViewModel<UserWithdrawalForLeaderChangeState, UserWithdrawalForLeaderChangeSideEffect>(
        UserWithdrawalForLeaderChangeState(),
    ) {
    private var pagingJob: Job? = null

    init {
        meetingEventBus.action
            .onEach { action ->
                intent {
                    when (action) {
                        is MeetingAction.MeetingUpdate -> {
                            val meetings =
                                state.meetings
                                    .map { meeting ->
                                        if (meeting.id == action.meeting.id) action.meeting else meeting
                                    }.filter { it.hostId != state.user.userId }

                            reduce { state.copy(meetings = meetings) }
                        }

                        is MeetingAction.MeetingDelete -> {
                            val meetings = state.meetings.filterNot { it.id == action.meetId }

                            reduce { state.copy(meetings = meetings) }
                        }

                        is MeetingAction.MeetingInvalidate -> {
                            getMeetings()
                        }

                        else -> {
                            return@intent
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    override suspend fun Syntax<UserWithdrawalForLeaderChangeState, UserWithdrawalForLeaderChangeSideEffect>.onContainerCreate() {
        // 모임장 필터링에 필요하므로 목록보다 먼저 채운다.
        val user = userRepository.getUser().first()
        reduce { state.copy(user = user) }

        getMeetings()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is UserWithdrawalForLeaderChangeIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is UserWithdrawalForLeaderChangeIntent.BackClick -> {
                    postSideEffect(UserWithdrawalForLeaderChangeSideEffect.NavigateToBack)
                }

                is UserWithdrawalForLeaderChangeIntent.MeetingClick -> {
                    postSideEffect(
                        UserWithdrawalForLeaderChangeSideEffect.NavigateToParticipantsForLeaderChange(intent.meetId),
                    )
                }

                is UserWithdrawalForLeaderChangeIntent.UserDeleteDialogShow -> {
                    reduce { state.copy(isShowExitDialog = intent.isShow) }
                }

                is UserWithdrawalForLeaderChangeIntent.UserDeleteClick -> {
                    deleteUser()
                }

                is UserWithdrawalForLeaderChangeIntent.RefreshClick,
                is UserWithdrawalForLeaderChangeIntent.NextPageLoad,
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
                        meetingRepository.getMeetingsForHost(
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

    private suspend fun Syntax<UserWithdrawalForLeaderChangeState, UserWithdrawalForLeaderChangeSideEffect>.handlePagingData(
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
                transform = { meetings -> meetings.filter { it.memberCount > 1 } },
            )

        reduce {
            state.copy(
                pagingInfo = result.pagingInfo,
                meetings = result.items,
            )
        }
    }

    private suspend fun Syntax<UserWithdrawalForLeaderChangeState, UserWithdrawalForLeaderChangeSideEffect>.deleteUser() {
        setLoading(true)

        try {
            userRepository.deleteUser()
            userRepository.clearMoimStorage()

            postSideEffect(UserWithdrawalForLeaderChangeSideEffect.NavigateToExit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (e is IOException) {
                postSideEffect(UserWithdrawalForLeaderChangeSideEffect.ShowNetworkErrorMessage)
            } else {
                postSideEffect(UserWithdrawalForLeaderChangeSideEffect.ShowServerErrorMessage)
            }
        } finally {
            setLoading(false)
        }
    }
}
