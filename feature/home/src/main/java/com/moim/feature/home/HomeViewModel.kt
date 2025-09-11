package com.moim.feature.home

import androidx.lifecycle.viewModelScope
import com.moim.core.common.delegate.MeetingAction
import com.moim.core.common.delegate.MeetingViewModelDelegate
import com.moim.core.common.delegate.PlanAction
import com.moim.core.common.delegate.PlanItemViewModelDelegate
import com.moim.core.common.delegate.meetingStateIn
import com.moim.core.common.delegate.planItemStateIn
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.Plan
import com.moim.core.common.model.item.asPlan
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.ToastMessage
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.common.view.restartableStateIn
import com.moim.core.data.datasource.plan.PlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    planRepository: PlanRepository,
    meetingViewModelDelegate: MeetingViewModelDelegate,
    planItemViewModelDelegate: PlanItemViewModelDelegate
) : BaseViewModel(),
    MeetingViewModelDelegate by meetingViewModelDelegate,
    PlanItemViewModelDelegate by planItemViewModelDelegate {

    private val meetingActionReceiver = meetingAction.meetingStateIn(viewModelScope)
    private val planActionReceiver = planItemAction.planItemStateIn(viewModelScope)

    private val meetingPlansResult = planRepository.getCurrentPlans()
        .asResult()
        .restartableStateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

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

                            is MeetingAction.MeetingUpdate -> {
                                val meetings = meetings.toMutableList().apply {
                                    withIndex()
                                        .firstOrNull { action.meeting.id == it.value.id }
                                        ?.index
                                        ?.let { index -> set(index, action.meeting) }
                                }

                                val plans = plans.map { plan ->
                                    if (plan.meetingId == action.meeting.id) {
                                        plan.copy(
                                            meetingName = action.meeting.name,
                                            meetingImageUrl = action.meeting.imageUrl,
                                        )
                                    } else {
                                        plan
                                    }
                                }

                                setUiState(copy(meetings = meetings, plans = plans))
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
                                                val newPlanTime = action.planItem.planAt
                                                val currentPlanTime = it.value.planAt
                                                newPlanTime.isBefore(currentPlanTime)
                                            }
                                            ?.let { add(it.index, action.planItem.asPlan()) }
                                            ?: run { add(action.planItem.asPlan()) }
                                    }.take(5)

                                setUiState(copy(plans = plans))
                            }

                            is PlanAction.PlanUpdate -> {
                                if (plans.isEmpty()) {
                                    meetingPlansResult.restart()
                                } else {
                                    val plans = plans.toMutableList().apply {
                                        withIndex()
                                            .firstOrNull { action.planItem.postId == it.value.planId }
                                            ?.index
                                            ?.let { index -> set(index, action.planItem.asPlan()) }
                                            ?: run { add(action.planItem.asPlan()) }
                                    }.sortedBy {
                                        it.planAt
                                    }.filter {
                                        it.isParticipant
                                    }

                                    setUiState(copy(plans = plans))
                                }
                            }

                            is PlanAction.PlanDelete -> {
                                val plans = plans.toMutableList().apply {
                                    withIndex()
                                        .firstOrNull { action.postId == it.value.planId }
                                        ?.index
                                        ?.let { index -> removeAt(index) }
                                }

                                setUiState(copy(plans = plans))
                            }

                            is PlanAction.PlanInvalidate -> meetingPlansResult.restart()

                            else -> return@collect
                        }
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: HomeUiAction) {
        when (uiAction) {
            is HomeUiAction.OnClickRefresh -> meetingPlansResult.restart()
            is HomeUiAction.OnClickAlarm -> setUiEvent(HomeUiEvent.NavigateToAlarm)
            is HomeUiAction.OnClickMeetingWrite -> setUiEvent(HomeUiEvent.NavigateToMeetingWrite)
            is HomeUiAction.OnClickPlanWrite -> navigateToPlanWrite()
            is HomeUiAction.OnClickPlanMore -> setUiEvent(HomeUiEvent.NavigateToCalendar)
            is HomeUiAction.OnClickPlan -> setUiEvent(HomeUiEvent.NavigateToPlanDetail(uiAction.planId, uiAction.isPlan))
            is HomeUiAction.OnUpdatePermissionCheck -> setPermissionCheck()
        }
    }

    private fun setPermissionCheck() {
        uiState.checkState<HomeUiState.Success> {
            setUiState(copy(isPermissionCheck = true))
        }
    }

    private fun navigateToPlanWrite() {
        uiState.checkState<HomeUiState.Success> {
            if (meetings.isEmpty()) {
                setUiEvent(HomeUiEvent.ShowToastMessage(ToastMessage.EmptyPlanErrorMessage))
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
        val isPermissionCheck: Boolean = false,
    ) : HomeUiState

    data object Error : HomeUiState
}

sealed interface HomeUiAction : UiAction {
    data object OnClickRefresh : HomeUiAction
    data object OnClickAlarm : HomeUiAction
    data object OnClickMeetingWrite : HomeUiAction
    data object OnClickPlanWrite : HomeUiAction
    data object OnClickPlanMore : HomeUiAction
    data class OnClickPlan(val planId: String, val isPlan: Boolean) : HomeUiAction
    data object OnUpdatePermissionCheck : HomeUiAction
}

sealed interface HomeUiEvent : UiEvent {
    data object NavigateToAlarm : HomeUiEvent
    data object NavigateToMeetingWrite : HomeUiEvent
    data object NavigateToPlanWrite : HomeUiEvent
    data object NavigateToCalendar : HomeUiEvent
    data class NavigateToPlanDetail(val planId: String, val isPlan: Boolean) : HomeUiEvent
    data class ShowToastMessage(val message: ToastMessage) : HomeUiEvent
}