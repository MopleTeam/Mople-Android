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
import com.moim.core.data.model.MeetingPlanResponse
import com.moim.core.model.MeetingPlan
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

    private val meetingPlansResult = loadDataSignal
        .flatMapLatest {
            meetingRepository.getMeetingPlans(
                page = 1,
                yearAndMonth = getDateTimeFormatZoneDate(pattern = "yyyyMM"),
                isClosed = false
            ).asResult()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            meetingPlansResult.collect { result ->
                when (result) {
                    is Result.Loading -> setUiState(HomeUiState.Loading)
                    is Result.Success -> setUiState(HomeUiState.Success(meetingPlans = result.data.map(MeetingPlanResponse::asItem)))
                    is Result.Error -> setUiState(HomeUiState.Error)
                }
            }
        }
    }

    fun onUiAction(uiAction: HomeUiAction) {
        when (uiAction) {
            is HomeUiAction.OnClickAlarm -> setUiEvent(HomeUiEvent.NavigateToAlarm)
            is HomeUiAction.OnClickWriteMeeting -> setUiEvent(HomeUiEvent.NavigateToWriteMeeting)
            is HomeUiAction.OnClickWritePlan -> setUiEvent(HomeUiEvent.NavigateToWritePlan)
            is HomeUiAction.OnClickMeetingMore -> setUiEvent(HomeUiEvent.NavigateToCalendar)
            is HomeUiAction.OnClickMeeting -> setUiEvent(HomeUiEvent.NavigateToMeetingDetail(uiAction.meetingId))
            is HomeUiAction.OnClickRefresh -> onRefresh()
        }
    }
}

sealed interface HomeUiState : UiState {
    data object Loading : HomeUiState

    data class Success(
        val meetingPlans: List<MeetingPlan> = emptyList()
    ) : HomeUiState

    data object Error : HomeUiState
}

sealed interface HomeUiAction : UiAction {
    data object OnClickAlarm : HomeUiAction
    data object OnClickWriteMeeting : HomeUiAction
    data object OnClickWritePlan : HomeUiAction
    data object OnClickMeetingMore : HomeUiAction
    data class OnClickMeeting(val meetingId: String) : HomeUiAction
    data object OnClickRefresh : HomeUiAction
}

sealed interface HomeUiEvent : UiEvent {
    data object NavigateToAlarm : HomeUiEvent
    data object NavigateToWriteMeeting : HomeUiEvent
    data object NavigateToWritePlan : HomeUiEvent
    data object NavigateToCalendar : HomeUiEvent
    data class NavigateToMeetingDetail(val meetingId: String) : HomeUiEvent
}