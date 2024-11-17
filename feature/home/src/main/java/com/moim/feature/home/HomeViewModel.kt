package com.moim.feature.home

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.model.MeetingPlanResponse
import com.moim.core.data.model.MeetingResponse
import com.moim.core.designsystem.R
import com.moim.core.model.Meeting
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
    private val planRepository: PlanRepository,
) : BaseViewModel() {

    private val meetingPlansResult = loadDataSignal
        .flatMapLatest { planRepository.getCurrentPlans().asResult() }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            meetingPlansResult.collect { result ->
                when (result) {
                    is Result.Loading -> setUiState(HomeUiState.Loading)
                    is Result.Success -> setUiState(
                        HomeUiState.Success(
                            plans = result.data.plans.map(MeetingPlanResponse::asItem),
                            meetings = result.data.meetings.map(MeetingResponse::asItem)
                        )
                    )

                    is Result.Error -> setUiState(HomeUiState.Error)
                }
            }
        }
    }

    fun onUiAction(uiAction: HomeUiAction) {
        when (uiAction) {
            is HomeUiAction.OnClickAlarm -> setUiEvent(HomeUiEvent.NavigateToAlarm)
            is HomeUiAction.OnClickMeetingWrite -> setUiEvent(HomeUiEvent.NavigateToMeetingWrite)
            is HomeUiAction.OnClickPlanWrite -> navigateToPlanWrite()
            is HomeUiAction.OnClickMeetingMore -> setUiEvent(HomeUiEvent.NavigateToCalendar)
            is HomeUiAction.OnClickMeeting -> setUiEvent(HomeUiEvent.NavigateToMeetingDetail(uiAction.meetingId))
            is HomeUiAction.OnClickRefresh -> onRefresh()
        }
    }

    private fun navigateToPlanWrite() {
        uiState.checkState<HomeUiState.Success> {
            if (meetings.isEmpty()) {
                setUiEvent(HomeUiEvent.ShowToastMessage(R.string.home_new_plan_created_not))
            } else {
                setUiEvent(HomeUiEvent.NavigateToPlanWrite)
            }
        }
    }
}

sealed interface HomeUiState : UiState {
    data object Loading : HomeUiState

    data class Success(
        val plans: List<MeetingPlan> = emptyList(),
        val meetings: List<Meeting> = emptyList(),
    ) : HomeUiState

    data object Error : HomeUiState
}

sealed interface HomeUiAction : UiAction {
    data object OnClickAlarm : HomeUiAction
    data object OnClickMeetingWrite : HomeUiAction
    data object OnClickPlanWrite : HomeUiAction
    data object OnClickMeetingMore : HomeUiAction
    data class OnClickMeeting(val meetingId: String) : HomeUiAction
    data object OnClickRefresh : HomeUiAction
}

sealed interface HomeUiEvent : UiEvent {
    data object NavigateToAlarm : HomeUiEvent
    data object NavigateToMeetingWrite : HomeUiEvent
    data object NavigateToPlanWrite : HomeUiEvent
    data object NavigateToCalendar : HomeUiEvent
    data class NavigateToMeetingDetail(val meetingId: String) : HomeUiEvent
    data class ShowToastMessage(@StringRes val messageRes: Int) : HomeUiEvent
}