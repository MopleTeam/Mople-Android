package com.moim.feature.participantlist

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.model.User
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.domain.usecase.GetParticipantsUseCase
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException

@HiltViewModel(assistedFactory = ParticipantListViewModel.Factory::class)
class ParticipantListViewModel @AssistedInject constructor(
    private val meetingRepository: MeetingRepository,
    private val planRepository: PlanRepository,
    private val reviewRepository: ReviewRepository,
    getParticipantsUseCase: GetParticipantsUseCase,
    @Assisted val viewIdType : ViewIdType,
) : BaseViewModel() {

    private val participants = getParticipantsUseCase(
        params = GetParticipantsUseCase.Params(
            id = viewIdType.id,
            isMeeting = viewIdType is ViewIdType.MeetId,
            isPlan = viewIdType is ViewIdType.PlanId,
        )
    ).cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            setUiState(
                ParticipantListUiState(
                    isMeeting = viewIdType is ViewIdType.MeetId,
                    participant = participants,
                    totalCount = getParticipantTotalCount()
                )
            )
        }
    }

    fun onUiAction(uiAction: ParticipantListUiAction) {
        when (uiAction) {
            is ParticipantListUiAction.OnClickBack -> setUiEvent(ParticipantListUiEvent.NavigateToBack)
            is ParticipantListUiAction.OnClickUserImage -> setUiEvent(ParticipantListUiEvent.NavigateToImageViewer(uiAction.userImage, uiAction.userName))
            is ParticipantListUiAction.OnClickMeetingInvite -> getInviteLink()
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
                        is Result.Loading -> return@collect
                        is Result.Success -> setUiEvent(ParticipantListUiEvent.NavigateToExternalShareUrl(result.data))
                        is Result.Error -> when (result.exception) {
                            is IOException -> setUiEvent(ParticipantListUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                            is NetworkException -> setUiEvent(ParticipantListUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                        }
                    }
                }
        }
    }

    private suspend fun getParticipantTotalCount(): Int {
        val totalCount = runCatching {
            when(viewIdType) {
                 is ViewIdType.MeetId -> meetingRepository.getMeetingParticipants(
                    meetingId = viewIdType.id,
                    cursor = "",
                    size = 1
                )

                is ViewIdType.PlanId -> planRepository.getPlanParticipants(
                    planId = viewIdType.id,
                    cursor = "",
                    size = 1
                )

                is ViewIdType.ReviewId -> reviewRepository.getReviewParticipants(
                    reviewId = viewIdType.id,
                    cursor = "",
                    size = 1
                )

                else -> throw IllegalStateException("this ViewTypeId is not allowed")
            }.totalCount
        }.getOrElse { 0 }

        return totalCount
    }

    @AssistedFactory
    interface Factory {
        fun create(
            viewIdType: ViewIdType,
        ): ParticipantListViewModel
    }
}

data class ParticipantListUiState(
    val isMeeting: Boolean,
    val participant: Flow<PagingData<User>>? = null,
    val totalCount: Int = 0,
) : UiState

sealed interface ParticipantListUiAction : UiAction {
    data object OnClickBack : ParticipantListUiAction

    data object OnClickMeetingInvite : ParticipantListUiAction

    data class OnClickUserImage(
        val userImage: String,
        val userName: String
    ) : ParticipantListUiAction
}

sealed interface ParticipantListUiEvent : UiEvent {
    data object NavigateToBack : ParticipantListUiEvent

    data class NavigateToImageViewer(
        val userImage: String,
        val userName: String
    ) : ParticipantListUiEvent

    data class NavigateToExternalShareUrl(
        val url: String
    ) : ParticipantListUiEvent

    data class ShowToastMessage(val toastMessage: ToastMessage) : ParticipantListUiEvent
}