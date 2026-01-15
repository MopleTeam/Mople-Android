package com.moim.feature.home

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.Plan
import com.moim.core.common.model.User
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.asPlan
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.eventbus.actionStateIn
import com.moim.core.ui.view.BaseViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.core.ui.view.UiAction
import com.moim.core.ui.view.UiEvent
import com.moim.core.ui.view.UiState
import com.moim.core.ui.view.checkState
import com.moim.core.ui.view.restartableStateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    userRepository: UserRepository,
    planRepository: PlanRepository,
    meetingEventBus: EventBus<MeetingAction>,
    planEventBus: EventBus<PlanAction>,
) : BaseViewModel() {
    private val meetingActionReceiver =
        meetingEventBus
            .action
            .actionStateIn(viewModelScope, MeetingAction.None)
    private val planActionReceiver =
        planEventBus
            .action
            .actionStateIn(viewModelScope, PlanAction.None)

    private val meetingPlansResult =
        combine(
            userRepository.getUser(),
            planRepository.getCurrentPlans(),
            ::Pair,
        ).mapLatest { (user, meetContainer) -> user to meetContainer }
            .asResult()
            .restartableStateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            launch {
                meetingPlansResult.collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            setUiState(HomeUiState.Loading)
                        }

                        is Result.Success -> {
                            val (user, meetContainer) = result.data

                            setUiState(
                                HomeUiState.Success(
                                    user = user,
                                    plans = meetContainer.plans,
                                    meetings = meetContainer.meetings,
                                ),
                            )
                        }

                        is Result.Error -> {
                            setUiState(HomeUiState.Error)
                        }
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
                                val meetings =
                                    meetings.toMutableList().apply {
                                        withIndex()
                                            .firstOrNull { action.meeting.id == it.value.id }
                                            ?.index
                                            ?.let { index -> set(index, action.meeting) }
                                    }

                                val plans =
                                    plans.map { plan ->
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

                            else -> {
                                return@collect
                            }
                        }
                    }
                }
            }

            launch {
                planActionReceiver.collect { action ->
                    uiState.checkState<HomeUiState.Success> {
                        when (action) {
                            is PlanAction.PlanCreate -> {
                                val plans =
                                    plans
                                        .toMutableList()
                                        .apply {
                                            withIndex()
                                                .firstOrNull {
                                                    val newPlanTime = action.planItem.planAt
                                                    val currentPlanTime = it.value.planAt
                                                    newPlanTime.isBefore(currentPlanTime)
                                                }?.let { add(it.index, action.planItem.asPlan()) }
                                                ?: run { add(action.planItem.asPlan()) }
                                        }.take(5)

                                setUiState(copy(plans = plans))
                            }

                            is PlanAction.PlanUpdate -> {
                                if (plans.isEmpty()) {
                                    meetingPlansResult.restart()
                                } else {
                                    val plans =
                                        plans
                                            .toMutableList()
                                            .apply {
                                                withIndex()
                                                    .firstOrNull { action.planItem.postId == it.value.planId }
                                                    ?.index
                                                    ?.let { index -> set(index, action.planItem.asPlan()) }
                                                    ?: run { add(action.planItem.asPlan()) }
                                            }.sortedBy {
                                                it.planAt
                                            }.filter {
                                                it.isParticipant || it.userId == user.userId
                                            }

                                    setUiState(copy(plans = plans))
                                }
                            }

                            is PlanAction.PlanDelete -> {
                                val plans =
                                    plans.toMutableList().apply {
                                        withIndex()
                                            .firstOrNull { action.postId == it.value.planId }
                                            ?.index
                                            ?.let { index -> removeAt(index) }
                                    }

                                setUiState(copy(plans = plans))
                            }

                            is PlanAction.PlanInvalidate -> {
                                meetingPlansResult.restart()
                            }

                            else -> {
                                return@collect
                            }
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
            is HomeUiAction.OnClickPlan -> setUiEvent(HomeUiEvent.NavigateToPlanDetail(ViewIdType.PlanId(uiAction.planId)))
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
        val user: User,
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

    data class OnClickPlan(
        val planId: String,
        val isPlan: Boolean,
    ) : HomeUiAction

    data object OnUpdatePermissionCheck : HomeUiAction
}

sealed interface HomeUiEvent : UiEvent {
    data object NavigateToAlarm : HomeUiEvent

    data object NavigateToMeetingWrite : HomeUiEvent

    data object NavigateToPlanWrite : HomeUiEvent

    data object NavigateToCalendar : HomeUiEvent

    data class NavigateToPlanDetail(
        val viewIdType: ViewIdType,
    ) : HomeUiEvent

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : HomeUiEvent
}
