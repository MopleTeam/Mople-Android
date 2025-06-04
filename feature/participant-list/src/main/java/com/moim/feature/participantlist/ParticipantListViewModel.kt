package com.moim.feature.participantlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.ToastMessage
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.restartableStateIn
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.model.Participant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ParticipantListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val meetingRepository: MeetingRepository,
    planRepository: PlanRepository,
    reviewRepository: ReviewRepository,
) : BaseViewModel() {

    private val isMeeting
        get() = savedStateHandle.get<Boolean>(KEY_IS_MEETING) ?: false

    private val isPlan
        get() = savedStateHandle.get<Boolean>(KEY_IS_PLAN) ?: false

    private val id
        get() = savedStateHandle.get<String>(KEY_ID) ?: ""

    private val participantListResult =
        when {
            isMeeting -> meetingRepository.getMeetingParticipants(id)
            isPlan -> planRepository.getPlanParticipants(id)
            else -> reviewRepository.getReviewParticipants(id)
        }.asResult().restartableStateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            participantListResult.collect { result ->
                when (result) {
                    is Result.Loading -> setUiState(ParticipantListUiState.Loading)
                    is Result.Success -> setUiState(ParticipantListUiState.Success(isMeeting, result.data))
                    is Result.Error -> setUiState(ParticipantListUiState.Error)
                }
            }
        }
    }

    fun onUiAction(uiAction: ParticipantListUiAction) {
        when (uiAction) {
            is ParticipantListUiAction.OnClickBack -> setUiEvent(ParticipantListUiEvent.NavigateToBack)
            is ParticipantListUiAction.OnClickRefresh -> participantListResult.restart()
            is ParticipantListUiAction.OnClickUserImage -> setUiEvent(ParticipantListUiEvent.NavigateToImageViewer(uiAction.userImage, uiAction.userName))
            is ParticipantListUiAction.OnClickMeetingInvite -> getInviteLink()
        }
    }

    private fun getInviteLink() {
        viewModelScope.launch {
            meetingRepository
                .getMeetingInviteCode(id)
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

    companion object {
        private const val KEY_IS_MEETING = "isMeeting"
        private const val KEY_IS_PLAN = "isPlan"
        private const val KEY_ID = "id"
    }
}

sealed interface ParticipantListUiState : UiState {
    data object Loading : ParticipantListUiState

    data class Success(
        val isMeeting: Boolean,
        val participant: List<Participant>
    ) : ParticipantListUiState

    data object Error : ParticipantListUiState
}

sealed interface ParticipantListUiAction : UiAction {
    data object OnClickBack : ParticipantListUiAction

    data object OnClickRefresh : ParticipantListUiAction

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