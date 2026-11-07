package com.moim.feature.participantlistforleaderchange

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.cancelIfActive
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.PagingHelper
import com.moim.feature.participantlistforleaderchange.model.ParticipantListForLeaderChangeIntent
import com.moim.feature.participantlistforleaderchange.model.ParticipantListForLeaderChangeSideEffect
import com.moim.feature.participantlistforleaderchange.model.ParticipantListForLeaderChangeState
import com.moim.feature.participantlistforleaderchange.model.ParticipantListUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.syntax.Syntax

@HiltViewModel(assistedFactory = ParticipantListForLeaderChangeViewModel.Factory::class)
class ParticipantListForLeaderChangeViewModel @AssistedInject constructor(
    private val meetingRepository: MeetingRepository,
    private val eventBus: EventBus<MeetingAction>,
    @Assisted val participantListForLeaderChangeRoute: DetailRoute.ParticipantListForLeaderChange,
) : MVIViewModel<ParticipantListForLeaderChangeState, ParticipantListForLeaderChangeSideEffect>(
        ParticipantListForLeaderChangeState(),
    ) {
    private val meetId = participantListForLeaderChangeRoute.meetId
    private var pagingJob: Job? = null

    override suspend fun Syntax<ParticipantListForLeaderChangeState, ParticipantListForLeaderChangeSideEffect>.onContainerCreate() {
        // snapshotFlow가 현재 값을 즉시 흘려서 최초 목록 로드까지 겸한다.
        observeKeywordChanges(state.keywordState)
    }

    override fun onIntent(intent: Intent) {
        if (intent !is ParticipantListForLeaderChangeIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is ParticipantListForLeaderChangeIntent.BackClick -> {
                    postSideEffect(ParticipantListForLeaderChangeSideEffect.NavigateToBack)
                }

                is ParticipantListForLeaderChangeIntent.RefreshClick,
                is ParticipantListForLeaderChangeIntent.NextPageLoad,
                -> {
                    getMeetingParticipantsForSearch(
                        keyword = state.keywordState.text.toString(),
                        cursor = state.pagingInfo.nextCursor,
                    )
                }

                is ParticipantListForLeaderChangeIntent.UserClick -> {
                    setSelectedUser(intent.user)
                }

                is ParticipantListForLeaderChangeIntent.UserProfileClick -> {
                    postSideEffect(ParticipantListForLeaderChangeSideEffect.NavigateToImageViewer(intent.user))
                }

                is ParticipantListForLeaderChangeIntent.LeaderChangeClick -> {
                    setLeaderChange(intent.userId)
                }

                is ParticipantListForLeaderChangeIntent.ChangeLeaderDialogShow -> {
                    reduce { state.copy(isShowChangeUserDialog = intent.isShow) }
                }
            }
        }
    }

    private fun observeKeywordChanges(keywordState: TextFieldState) {
        snapshotFlow { keywordState.text.toString() }
            .distinctUntilChanged()
            .debounce(500)
            .onEach { keyword ->
                pagingJob.cancelIfActive()
                getMeetingParticipantsForSearch(
                    keyword = keyword.trim(),
                    cursor = null,
                )
            }.launchIn(viewModelScope)
    }

    private fun getMeetingParticipantsForSearch(
        keyword: String = "",
        cursor: String? = null,
    ) {
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
                        meetingRepository.getMeetingParticipantsForSearch(
                            meetingId = meetId.id,
                            keyword = keyword,
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

    private suspend fun Syntax<ParticipantListForLeaderChangeState, ParticipantListForLeaderChangeSideEffect>.handlePagingData(
        pagingData: PaginationContainer<List<User>>?,
        isLoading: Boolean,
        cursor: String?,
    ) {
        val result =
            PagingHelper.handlePagingResult(
                pagingData = pagingData,
                isLoading = isLoading,
                currentPagingInfo = state.pagingInfo,
                currentItems = state.users,
                isInitialLoad = cursor == null,
                transform = { users ->
                    users
                        .filter { it.userRole != HOST_USER_ROLE }
                        .map {
                            ParticipantListUiModel(
                                user = it,
                                isSelected = false,
                            )
                        }
                },
            )

        reduce {
            state.copy(
                pagingInfo = result.pagingInfo,
                users = result.items,
            )
        }
    }

    private suspend fun Syntax<ParticipantListForLeaderChangeState, ParticipantListForLeaderChangeSideEffect>.setSelectedUser(
        selectedUser: User,
    ) {
        val users =
            state.users.map { user ->
                val isSelectedUser = user.user.userId == selectedUser.userId
                user.copy(isSelected = isSelectedUser && !user.isSelected)
            }

        reduce {
            state.copy(
                users = users,
                selectedUser = users.find { it.isSelected }?.user,
            )
        }
    }

    private suspend fun Syntax<ParticipantListForLeaderChangeState, ParticipantListForLeaderChangeSideEffect>.setLeaderChange(
        userId: String,
    ) {
        setLoading(true)

        try {
            meetingRepository.updateMeetingLeader(
                meetingId = meetId.id,
                newHostId = userId,
            )

            eventBus.send(MeetingAction.MeetingInvalidate())
            postSideEffect(ParticipantListForLeaderChangeSideEffect.ShowCompletedMessage)
            postSideEffect(ParticipantListForLeaderChangeSideEffect.NavigateToExit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            postSideEffect(ParticipantListForLeaderChangeSideEffect.ShowErrorMessage)
        } finally {
            setLoading(false)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(participantListRoute: DetailRoute.ParticipantListForLeaderChange): ParticipantListForLeaderChangeViewModel
    }

    companion object {
        private const val HOST_USER_ROLE = "HOST"
    }
}
