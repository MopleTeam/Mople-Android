package com.moim.feature.participantlistforleaderchange

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.cancelIfActive
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.PagingUiState
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import com.moim.feature.participantlistforleaderchange.model.ParticipantListUiModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ParticipantListForLeaderChangeViewModel.Factory::class)
class ParticipantListForLeaderChangeViewModel @AssistedInject constructor(
    private val meetingRepository: MeetingRepository,
    private val eventBus: EventBus<MeetingAction>,
    @Assisted val participantListForLeaderChangeRoute: DetailRoute.ParticipantListForLeaderChange,
) : BaseViewModel() {
    private val meetId = participantListForLeaderChangeRoute.meetId
    private var pagingJob: Job? = null

    val keywordFieldState = TextFieldState()

    init {
        viewModelScope.launch {
            setUiState(ParticipantListForLeaderChangeUiState())
            observeKeywordChanges()
        }
    }

    fun onUiAction(uiAction: ParticipantListForLeaderChangeUiAction) {
        when (uiAction) {
            is ParticipantListForLeaderChangeUiAction.OnClickBack -> {
                setUiEvent(ParticipantListForLeaderChangeUiEvent.NavigateToBack)
            }

            is ParticipantListForLeaderChangeUiAction.OnLoadNextPage -> {
                val uiState = uiState.value as? ParticipantListForLeaderChangeUiState ?: return
                getMeetingParticipantsForSearch(
                    keyword = keywordFieldState.text.toString(),
                    cursor = uiState.pagingInfo.nextCursor,
                )
            }

            is ParticipantListForLeaderChangeUiAction.OnClickRefresh -> {
                val uiState = uiState.value as? ParticipantListForLeaderChangeUiState ?: return
                getMeetingParticipantsForSearch(
                    keyword = keywordFieldState.text.toString(),
                    cursor = uiState.pagingInfo.nextCursor,
                )
            }

            is ParticipantListForLeaderChangeUiAction.OnClickUser -> {
                setSelectedUser(uiAction.user)
            }

            is ParticipantListForLeaderChangeUiAction.OnClickUserProfile -> {
                setUiEvent(ParticipantListForLeaderChangeUiEvent.NavigateToImageViewer(uiAction.user))
            }

            is ParticipantListForLeaderChangeUiAction.OnClickLeaderChange -> {
                setLeaderChange(uiAction.userId)
            }

            is ParticipantListForLeaderChangeUiAction.ShowChangeLeaderDialog -> {
                uiState.checkState<ParticipantListForLeaderChangeUiState> {
                    setUiState(copy(isShowChangeUserDialog = uiAction.isShow))
                }
            }
        }
    }

    private fun observeKeywordChanges() {
        snapshotFlow {
            keywordFieldState
                .text
                .toString()
        }.filterNotNull()
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
            viewModelScope.launch {
                handlePagingData(
                    pagingInfo = null,
                    isLoading = true,
                    cursor = cursor,
                )

                val pagingInfo =
                    runCatching {
                        meetingRepository.getMeetingParticipantsForSearch(
                            meetingId = meetId.id,
                            keyword = keyword,
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
        pagingInfo: PaginationContainer<List<User>>?,
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
        pagingData: PaginationContainer<List<User>>?,
        isLoading: Boolean,
    ) {
        val uiState =
            when {
                isLoading -> {
                    ParticipantListForLeaderChangeUiState(
                        pagingInfo =
                            PagingUiState(
                                isLoading = true,
                            ),
                    )
                }

                pagingData == null -> {
                    ParticipantListForLeaderChangeUiState(
                        pagingInfo =
                            PagingUiState(
                                isLoading = false,
                                isError = true,
                            ),
                    )
                }

                else -> {
                    val data =
                        pagingData
                            .content
                            .filter { it.userRole != "HOST" }
                            .map {
                                ParticipantListUiModel(
                                    user = it,
                                    isSelected = false,
                                )
                            }

                    ParticipantListForLeaderChangeUiState(
                        pagingInfo =
                            PagingUiState(
                                isLoading = false,
                                nextCursor = pagingData.page.nextCursor,
                                isLast = !pagingData.page.isNext || data.isEmpty(),
                            ),
                        users = data,
                    )
                }
            }

        setUiState(uiState)
    }

    private fun addLoadPagingData(
        pagingData: PaginationContainer<List<User>>?,
        isLoading: Boolean,
    ) {
        uiState.checkState<ParticipantListForLeaderChangeUiState> {
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
                            users = users,
                        )
                    }

                    pagingData == null -> {
                        this.copy(
                            pagingInfo =
                                pagingInfo.copy(
                                    isLoadingFooter = false,
                                    isErrorFooter = true,
                                ),
                            users = users,
                        )
                    }

                    else -> {
                        val addData =
                            pagingData
                                .content
                                .filter { it.userRole != "HOST" }
                                .map {
                                    ParticipantListUiModel(
                                        user = it,
                                        isSelected = false,
                                    )
                                }

                        this.copy(
                            pagingInfo =
                                pagingInfo.copy(
                                    isLoadingFooter = false,
                                    isErrorFooter = false,
                                    nextCursor = pagingData.page.nextCursor,
                                    isLast = !pagingData.page.isNext || addData.isEmpty(),
                                    totalCount = pagingData.totalCount,
                                ),
                            users =
                                users
                                    .toMutableList()
                                    .apply { addAll(addData) },
                        )
                    }
                }

            setUiState(uiState)
        }
    }

    private fun setSelectedUser(selectedUser: User) {
        uiState.checkState<ParticipantListForLeaderChangeUiState> {
            val users =
                users.map { user ->
                    val isSelectedUser = user.user.userId == selectedUser.userId
                    user.copy(isSelected = isSelectedUser && !user.isSelected)
                }

            setUiState(
                copy(
                    users = users,
                    selectedUser = users.find { it.isSelected }?.user,
                ),
            )
        }
    }

    private fun setLeaderChange(userId: String) {
        viewModelScope.launch {
            uiState.checkState<ParticipantListForLeaderChangeUiState> {
                meetingRepository
                    .updateMeetingLeader(
                        meetingId = meetId.id,
                        newHostId = userId,
                    ).asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> {
                                return@collect
                            }

                            is Result.Success -> {
                                eventBus.send(MeetingAction.MeetingInvalidate())
                                setUiEvent(ParticipantListForLeaderChangeUiEvent.ShowCompletedMessage)
                                setUiEvent(ParticipantListForLeaderChangeUiEvent.NavigateToExit)
                            }

                            is Result.Error -> {
                                setUiEvent(ParticipantListForLeaderChangeUiEvent.ShowErrorMessage)
                            }
                        }
                    }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(participantListRoute: DetailRoute.ParticipantListForLeaderChange): ParticipantListForLeaderChangeViewModel
    }
}

data class ParticipantListForLeaderChangeUiState(
    val pagingInfo: PagingUiState = PagingUiState(),
    val users: List<ParticipantListUiModel> = emptyList(),
    val selectedUser: User? = null,
    val isShowChangeUserDialog: Boolean = false,
) : UiState

sealed interface ParticipantListForLeaderChangeUiAction : UiAction {
    data object OnClickBack : ParticipantListForLeaderChangeUiAction

    data class OnClickUser(
        val user: User,
    ) : ParticipantListForLeaderChangeUiAction

    data class OnClickUserProfile(
        val user: User,
    ) : ParticipantListForLeaderChangeUiAction

    data class OnClickLeaderChange(
        val userId: String,
    ) : ParticipantListForLeaderChangeUiAction

    data class ShowChangeLeaderDialog(
        val isShow: Boolean,
    ) : ParticipantListForLeaderChangeUiAction

    data object OnLoadNextPage : ParticipantListForLeaderChangeUiAction

    data object OnClickRefresh : ParticipantListForLeaderChangeUiAction
}

sealed interface ParticipantListForLeaderChangeUiEvent : UiEvent {
    data object NavigateToBack : ParticipantListForLeaderChangeUiEvent

    data class NavigateToImageViewer(
        val user: User,
    ) : ParticipantListForLeaderChangeUiEvent

    data object NavigateToExit : ParticipantListForLeaderChangeUiEvent

    data object ShowCompletedMessage : ParticipantListForLeaderChangeUiEvent

    data object ShowErrorMessage : ParticipantListForLeaderChangeUiEvent
}
