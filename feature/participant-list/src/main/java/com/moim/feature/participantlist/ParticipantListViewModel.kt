package com.moim.feature.participantlist

import androidx.lifecycle.viewModelScope
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.model.PaginationContainer
import com.moim.core.common.model.User
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.ui.route.DetailRoute
import com.moim.core.ui.util.isActiveCheck
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.PagingHelper
import com.moim.core.ui.view.PagingUiState
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException

@HiltViewModel(assistedFactory = ParticipantListViewModel.Factory::class)
class ParticipantListViewModel @AssistedInject constructor(
    private val meetingRepository: MeetingRepository,
    private val planRepository: PlanRepository,
    private val reviewRepository: ReviewRepository,
    @Assisted val participantListRoute: DetailRoute.ParticipantList,
) : BaseViewModel() {
    private var pagingJob: Job? = null
    private val viewIdType = participantListRoute.viewIdType

    init {
        viewModelScope.launch {
            setUiState(ParticipantListUiState(isMeeting = viewIdType is ViewIdType.MeetId))
            getParticipants()
        }
    }

    fun onUiAction(uiAction: ParticipantListUiAction) {
        when (uiAction) {
            is ParticipantListUiAction.OnClickBack -> {
                setUiEvent(ParticipantListUiEvent.NavigateToBack)
            }

            is ParticipantListUiAction.OnLoadNextPage -> {
                val uiState = uiState.value as? ParticipantListUiState ?: return
                getParticipants(uiState.pagingInfo.nextCursor)
            }

            is ParticipantListUiAction.OnClickRefresh -> {
                val uiState = uiState.value as? ParticipantListUiState ?: return
                getParticipants(uiState.pagingInfo.nextCursor)
            }

            is ParticipantListUiAction.OnClickUserImage -> {
                setUiEvent(
                    ParticipantListUiEvent.NavigateToImageViewer(uiAction.userImage, uiAction.userName),
                )
            }

            is ParticipantListUiAction.OnClickMeetingInvite -> {
                getInviteLink()
            }
        }
    }

    private fun getParticipants(cursor: String? = null) {
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
                        when (viewIdType) {
                            is ViewIdType.MeetId -> {
                                meetingRepository.getMeetingParticipants(
                                    meetingId = viewIdType.id,
                                    cursor = cursor ?: "",
                                    size = 30,
                                )
                            }

                            is ViewIdType.PlanId -> {
                                planRepository.getPlanParticipants(
                                    planId = viewIdType.id,
                                    cursor = cursor ?: "",
                                    size = 30,
                                )
                            }

                            is ViewIdType.ReviewId -> {
                                reviewRepository.getReviewParticipants(
                                    reviewId = viewIdType.id,
                                    cursor = cursor ?: "",
                                    size = 30,
                                )
                            }

                            else -> {
                                throw IllegalStateException("this ViewTypeId is not allowed")
                            }
                        }
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
        uiState.checkState<ParticipantListUiState> {
            val result =
                PagingHelper.handlePagingResult(
                    pagingData = pagingInfo,
                    isLoading = isLoading,
                    currentPagingInfo = this.pagingInfo,
                    currentItems = participants,
                    isInitialLoad = cursor == null,
                    transform = { users -> users },
                )

            setUiState(
                copy(
                    pagingInfo = result.pagingInfo,
                    participants = result.items,
                ),
            )
        }
    }

    private fun getInviteLink() {
        viewModelScope.launch {
            meetingRepository
                .getMeetingInviteCode(viewIdType.id)
                .asResult()
                .onEach { setLoading(it is Result.Loading) }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            return@collect
                        }

                        is Result.Success -> {
                            setUiEvent(ParticipantListUiEvent.NavigateToExternalShareUrl(result.data))
                        }

                        is Result.Error -> {
                            when (result.exception) {
                                is IOException -> setUiEvent(ParticipantListUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                                is NetworkException -> setUiEvent(ParticipantListUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                            }
                        }
                    }
                }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(participantListRoute: DetailRoute.ParticipantList): ParticipantListViewModel
    }
}

data class ParticipantListUiState(
    val isMeeting: Boolean = true,
    val pagingInfo: PagingUiState = PagingUiState(),
    val participants: List<User> = emptyList(),
) : UiState

sealed interface ParticipantListUiAction : UiAction {
    data object OnClickBack : ParticipantListUiAction

    data object OnClickRefresh : ParticipantListUiAction

    data object OnClickMeetingInvite : ParticipantListUiAction

    data class OnClickUserImage(
        val userImage: String,
        val userName: String,
    ) : ParticipantListUiAction

    data object OnLoadNextPage : ParticipantListUiAction
}

sealed interface ParticipantListUiEvent : UiEvent {
    data object NavigateToBack : ParticipantListUiEvent

    data class NavigateToImageViewer(
        val userImage: String,
        val userName: String,
    ) : ParticipantListUiEvent

    data class NavigateToExternalShareUrl(
        val url: String,
    ) : ParticipantListUiEvent

    data class ShowToastMessage(
        val toastMessage: ToastMessage,
    ) : ParticipantListUiEvent
}
