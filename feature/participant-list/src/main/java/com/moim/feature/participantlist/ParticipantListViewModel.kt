package com.moim.feature.participantlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.model.Participant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParticipantListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val planRepository: PlanRepository,
    private val reviewRepository: ReviewRepository,
    private val meetingRepository: MeetingRepository,
) : BaseViewModel() {

    private val isMeeting
        get() = savedStateHandle.get<Boolean>(KEY_IS_MEETING) ?: false

    private val isPlan
        get() = savedStateHandle.get<Boolean>(KEY_IS_PLAN) ?: false

    private val id
        get() = savedStateHandle.get<String>(KEY_ID) ?: ""

    private val participantListResult = loadDataSignal
        .flatMapLatest {
            when {
                isMeeting -> meetingRepository.getMeetingParticipants(id)
                isPlan -> planRepository.getPlanParticipants(id)
                else -> reviewRepository.getReviewParticipants(id)
            }.asResult()
        }.stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

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
            is ParticipantListUiAction.OnClickRefresh -> onRefresh()
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
}

sealed interface ParticipantListUiEvent : UiEvent {
    data object NavigateToBack : ParticipantListUiEvent
}