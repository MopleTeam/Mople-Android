package com.moim.feature.home

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.moim.core.common.delegate.MeetingAction
import com.moim.core.common.delegate.MeetingViewModelDelegate
import com.moim.core.common.delegate.PlanAction
import com.moim.core.common.delegate.PlanViewModelDelegate
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.util.parseZonedDateTimeForDateString
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.designsystem.R
import com.moim.core.model.Meeting
import com.moim.core.model.Plan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    meetingViewModelDelegate: MeetingViewModelDelegate,
    planViewModelDelegate: PlanViewModelDelegate
) : BaseViewModel(),
    MeetingViewModelDelegate by meetingViewModelDelegate,
    PlanViewModelDelegate by planViewModelDelegate {

    private val meetingActionReceiver = meetingAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed((5000)), MeetingAction.None)

    private val planActionReceiver = planAction
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed((5000)), PlanAction.None)

    private val meetingPlansResult = loadDataSignal
        .flatMapLatest { planRepository.getCurrentPlans().asResult() }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            launch {
                meetingPlansResult.collect { result ->
                    when (result) {
                        is Result.Loading -> setUiState(HomeUiState.Loading)
                        is Result.Success -> setUiState(
                            HomeUiState.Success(
                                plans = result.data.plans,
                                meetings = result.data.meetings,
                            )
                        )

                        is Result.Error -> setUiState(HomeUiState.Error)
                    }
                }
            }

            launch {
                meetingActionReceiver.collect { action ->
                    uiState.checkState<HomeUiState.Success> {
                        when (action) {
                            is MeetingAction.MeetingCreate -> {
                                val meetings = meetings.toMutableList().apply { add(action.meeting) }
                                setUiState(copy(meetings = meetings))
                            }

                            is MeetingAction.MeetingDelete -> {
                                val meetings = meetings.toMutableList().apply {
                                    withIndex()
                                        .firstOrNull { action.meetId == it.value.id }
                                        ?.index
                                        ?.let { index -> removeAt(index) }
                                }

                                setUiState(copy(meetings = meetings))
                            }

                            else -> return@collect
                        }
                    }
                }
            }

            launch {
                planActionReceiver.collect { action ->
                    uiState.checkState<HomeUiState.Success> {
                        when (action) {
                            is PlanAction.PlanCreate -> {
                                val plans = plans.toMutableList()
                                    .apply {
                                        withIndex()
                                            .firstOrNull {
                                                val newPlanTime = action.plan.planTime.parseZonedDateTimeForDateString()
                                                val currentPlanTime = it.value.planTime.parseZonedDateTimeForDateString()
                                                newPlanTime.isBefore(currentPlanTime)
                                            }
                                            ?.let { add(it.index, action.plan) }
                                            ?: run { add(action.plan) }
                                    }.take(5)

                                setUiState(copy(plans = plans))
                            }

                            is PlanAction.PlanUpdate -> {
                                val plans = plans.toMutableList().apply {
                                    withIndex()
                                        .firstOrNull { action.plan.planId == it.value.planId }
                                        ?.index
                                        ?.let { index -> set(index, action.plan) }
                                }

                                setUiState(copy(plans = plans))
                            }

                            is PlanAction.PlanDelete -> {
                                val plans = plans.toMutableList().apply {
                                    withIndex()
                                        .firstOrNull { action.planId == it.value.planId }
                                        ?.index
                                        ?.let { index -> removeAt(index) }
                                }

                                setUiState(copy(plans = plans))
                            }

                            is PlanAction.PlanInvalidate -> onRefresh()

                            else -> return@collect
                        }
                    }
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
        val plans: List<Plan> = emptyList(),
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