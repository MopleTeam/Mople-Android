package com.moim.feature.meeting

import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.model.MeetingResponse
import com.moim.core.model.Meeting
import com.moim.core.model.Member
import com.moim.core.model.asItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MeetingViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository
) : BaseViewModel() {

    private val meetingsResult = loadDataSignal
        .flatMapLatest { meetingRepository.getMeetings().asResult() }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            meetingsResult.collect { result ->
                when (result) {
                    is Result.Loading -> setUiState(MeetingUiState.Loading)
                    is Result.Success -> setUiState(MeetingUiState.Success(result.data.map(MeetingResponse::asItem) + sample))
                    is Result.Error -> setUiState(MeetingUiState.Error)
                }
            }
        }
    }

    fun onUiAction(uiAction: MeetingUiAction) {
        when (uiAction) {
            is MeetingUiAction.OnClickMeetingWrite -> setUiEvent(MeetingUiEvent.NavigateToMeetingWrite)
            is MeetingUiAction.OnClickMeeting -> setUiEvent(MeetingUiEvent.NavigateToMeetingDetail(uiAction.meetingId))
            is MeetingUiAction.OnClickRefresh -> onRefresh()
        }
    }

    companion object {
        private val sample = listOf(
            Meeting(
                id = "1",
                name = "우리중학교 동창1",
                imageUrl = "https://plus.unsplash.com/premium_photo-1698507574126-7135d2684aa2",
                members = listOf(Member(), Member(), Member()),
                creatorId = "",
                lastPlanAt = ZonedDateTime.now().plusDays(10).toString(),
            ),
            Meeting(
                id = "2",
                name = "우리중학교 동창2",
                imageUrl = "https://images.unsplash.com/photo-1730829807497-9c5b8c9c41c4",
                members = listOf(Member(), Member(), Member()),
                creatorId = "",
                lastPlanAt = ZonedDateTime.now().minusDays(10).toString(),
            ),
            Meeting(
                id = "3",
                name = "우리중학교 동창3",
                imageUrl = "https://images.unsplash.com/photo-1730812393789-a7d15960029d",
                members = listOf(Member(), Member(), Member()),
                creatorId = "",
            ),
            Meeting(
                id = "4",
                name = "우리중학교 동창4",
                imageUrl = "https://plus.unsplash.com/premium_photo-1670333183316-ab697ddd9b13",
                members = listOf(Member(), Member(), Member()),
                creatorId = "",
                lastPlanAt = ZonedDateTime.now().toString()
            ),
        )
    }
}

sealed interface MeetingUiState : UiState {
    data object Loading : MeetingUiState
    data class Success(val meetings: List<Meeting>) : MeetingUiState
    data object Error : MeetingUiState
}

sealed interface MeetingUiAction : UiAction {
    data class OnClickMeeting(val meetingId: String) : MeetingUiAction
    data object OnClickMeetingWrite : MeetingUiAction
    data object OnClickRefresh : MeetingUiAction
}

sealed interface MeetingUiEvent : UiEvent {
    data object NavigateToMeetingWrite : MeetingUiEvent
    data class NavigateToMeetingDetail(val meetingId: String) : MeetingUiEvent
}