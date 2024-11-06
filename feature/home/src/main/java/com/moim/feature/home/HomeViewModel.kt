package com.moim.feature.home

import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.util.getDateTimeFormatZoneDate
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.model.MeetingResponse
import com.moim.core.model.MeetingInfo
import com.moim.core.model.asItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val meetingRepository: MeetingRepository,
) : BaseViewModel() {

    private val meetingsResult = loadDataSignal
        .flatMapLatest {
            meetingRepository.getMeetings(
                page = 1,
                yearAndMonth = getDateTimeFormatZoneDate(pattern = "yyyyMM"),
                isClosed = false
            ).asResult()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            meetingsResult.collect { result ->
                when (result) {
                    is Result.Loading -> setUiState(HomeUiState.Loading)
                    is Result.Success -> setUiState(HomeUiState.Success(meetings = result.data.map(MeetingResponse::asItem)))
                    is Result.Error -> setUiState(HomeUiState.Error)
                }
            }
        }
    }

    fun onUiAction(uiAction: HomeUiAction) {
        when (uiAction) {
            is HomeUiAction.OnClickAlarm -> setUiEvent(HomeUiEvent.NavigateToAlarm)
            is HomeUiAction.OnClickWriteGroup -> setUiEvent(HomeUiEvent.NavigateToWriteGroup)
            is HomeUiAction.OnClickWriteMeeting -> setUiEvent(HomeUiEvent.NavigateToWriteMeeting)
            is HomeUiAction.OnClickMeetingMore -> setUiEvent(HomeUiEvent.NavigateToCalendar)
            is HomeUiAction.OnClickMeeting -> setUiEvent(HomeUiEvent.NavigateToMeetingDetail(uiAction.meetingId))
            is HomeUiAction.OnClickRefresh -> onRefresh()
        }
    }
}

sealed interface HomeUiState : UiState {
    data object Loading : HomeUiState

    data class Success(
        val meetings: List<MeetingInfo> = emptyList()
    ) : HomeUiState

    data object Error : HomeUiState
}

sealed interface HomeUiAction : UiAction {
    data object OnClickAlarm : HomeUiAction
    data object OnClickWriteGroup : HomeUiAction
    data object OnClickWriteMeeting : HomeUiAction
    data object OnClickMeetingMore : HomeUiAction
    data class OnClickMeeting(val meetingId: String) : HomeUiAction
    data object OnClickRefresh : HomeUiAction
}

sealed interface HomeUiEvent : UiEvent {
    data object NavigateToAlarm : HomeUiEvent
    data object NavigateToWriteGroup : HomeUiEvent
    data object NavigateToWriteMeeting : HomeUiEvent
    data object NavigateToCalendar : HomeUiEvent
    data class NavigateToMeetingDetail(val meetingId: String) : HomeUiEvent
}