package com.moim.feature.userwithdrawalforleaderchange

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.PagingHelper
import com.moim.core.ui.view.PagingUiState
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

@HiltViewModel
class UserWithdrawalForLeaderChangeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val meetingRepository: MeetingRepository,
    meetingEventBus: EventBus<MeetingAction>,
) : BaseViewModel() {
    private var pagingJob: Job? = null

    init {
        viewModelScope.launch {
            launch {
                val user = userRepository.getUser().first()
                setUiState(UserWithdrawalForLeaderChangeUiState(user = user))
                getMeetings()
            }

            launch {
                meetingEventBus.action.collect { action ->
                    uiState.checkState<UserWithdrawalForLeaderChangeUiState> {
                        when (action) {
                            is MeetingAction.MeetingUpdate -> {
                                val meetings =
                                    meetings
                                        .map { meeting ->
                                            if (meeting.id == action.meeting.id) {
                                                action.meeting
                                            } else {
                                                meeting
                                            }
                                        }.filter {
                                            it.creatorId != user.userId
                                        }

                                setUiState(copy(meetings = meetings))
                            }

                            is MeetingAction.MeetingInvalidate -> {
                                getMeetings()
                            }

                            is MeetingAction.MeetingDelete -> {
                                val meetings =
                                    meetings
                                        .toMutableList()
                                        .apply { removeIf { it.id == action.meetId } }

                                setUiState(copy(meetings = meetings))
                            }

                            else -> {
                                return@collect
                            }
                        }
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: UserWithdrawalForLeaderChangeUiAction) {
        when (uiAction) {
            is UserWithdrawalForLeaderChangeUiAction.OnClickBack -> {
                setUiEvent(UserWithdrawalForLeaderChangeUiEvent.NavigateToBack)
            }

            is UserWithdrawalForLeaderChangeUiAction.OnClickUserDelete -> {
                deleteUser()
            }

            is UserWithdrawalForLeaderChangeUiAction.OnClickMeeting -> {
                setUiEvent(UserWithdrawalForLeaderChangeUiEvent.NavigateToParticipantsForLeaderChange(uiAction.meetId))
            }

            is UserWithdrawalForLeaderChangeUiAction.OnShowUserDeleteDialog -> {
                showDeleteUserDialog(uiAction.isShow)
            }

            is UserWithdrawalForLeaderChangeUiAction.OnClickRefresh -> {
                val uiState = uiState.value as? UserWithdrawalForLeaderChangeUiState ?: return
                getMeetings(uiState.pagingInfo.nextCursor)
            }

            is UserWithdrawalForLeaderChangeUiAction.OnLoadNextPage -> {
                val uiState = uiState.value as? UserWithdrawalForLeaderChangeUiState ?: return
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
                        meetingRepository.getMeetingsForHost(
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
        uiState.checkState<UserWithdrawalForLeaderChangeUiState> {
            val result =
                PagingHelper.handlePagingResult(
                    pagingData = pagingInfo,
                    isLoading = isLoading,
                    currentPagingInfo = this.pagingInfo,
                    currentItems = meetings,
                    isInitialLoad = cursor == null,
                    transform = { meetings -> meetings },
                )

            setUiState(
                copy(
                    pagingInfo = result.pagingInfo,
                    meetings = result.items,
                ),
            )
        }
    }

    private fun showDeleteUserDialog(isShow: Boolean) {
        uiState.checkState<UserWithdrawalForLeaderChangeUiState> {
            setUiState(copy(isShowExitDialog = isShow))
        }
    }

    private fun deleteUser() {
        viewModelScope.launch {
            userRepository
                .deleteUser()
                .asResult()
                .onEach { result -> setLoading(result is Result.Loading) }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            return@collect
                        }

                        is Result.Success -> {
                            clearUserData()
                        }

                        is Result.Error -> {
                            when (result.exception) {
                                is IOException -> setUiEvent(UserWithdrawalForLeaderChangeUiEvent.ShowNetworkErrorMessage)
                                else -> setUiEvent(UserWithdrawalForLeaderChangeUiEvent.ShowServerErrorMessage)
                            }
                        }
                    }
                }
        }
    }

    private suspend fun clearUserData() {
        userRepository.clearMoimStorage()
        setUiEvent(UserWithdrawalForLeaderChangeUiEvent.NavigateToExit)
    }
}

data class UserWithdrawalForLeaderChangeUiState(
    val pagingInfo: PagingUiState = PagingUiState(),
    val user: User,
    val meetings: List<Meeting> = emptyList(),
    val isShowExitDialog: Boolean = false,
) : UiState

sealed interface UserWithdrawalForLeaderChangeUiAction : UiAction {
    data object OnClickBack : UserWithdrawalForLeaderChangeUiAction

    data class OnClickMeeting(
        val meetId: String,
    ) : UserWithdrawalForLeaderChangeUiAction

    data class OnShowUserDeleteDialog(
        val isShow: Boolean,
    ) : UserWithdrawalForLeaderChangeUiAction

    data object OnClickUserDelete : UserWithdrawalForLeaderChangeUiAction

    data object OnClickRefresh : UserWithdrawalForLeaderChangeUiAction

    data object OnLoadNextPage : UserWithdrawalForLeaderChangeUiAction
}

sealed interface UserWithdrawalForLeaderChangeUiEvent : UiEvent {
    data object NavigateToBack : UserWithdrawalForLeaderChangeUiEvent

    data object NavigateToExit : UserWithdrawalForLeaderChangeUiEvent

    data object ShowNetworkErrorMessage : UserWithdrawalForLeaderChangeUiEvent

    data object ShowServerErrorMessage : UserWithdrawalForLeaderChangeUiEvent

    data class NavigateToParticipantsForLeaderChange(
        val meetId: String,
    ) : UserWithdrawalForLeaderChangeUiEvent
}
