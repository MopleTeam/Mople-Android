package com.moim.feature.home

import androidx.lifecycle.viewModelScope
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.asPlan
import com.moim.core.common.result.Result
import com.moim.core.common.result.data
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.home.model.HomeIntent
import com.moim.feature.home.model.HomeSideEffect
import com.moim.feature.home.model.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.syntax.Syntax
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val planRepository: PlanRepository,
    meetingEventBus: EventBus<MeetingAction>,
    planEventBus: EventBus<PlanAction>,
) : MVIViewModel<HomeState, HomeSideEffect>(HomeState()) {
    init {
        meetingEventBus.action
            .onEach { action ->
                intent {
                    val currentPlans = state.plans.data ?: return@intent

                    when (action) {
                        is MeetingAction.MeetingCreate -> {
                            reduce { state.copy(hasJoinedMeet = true) }
                        }

                        is MeetingAction.MeetingUpdate -> {
                            val plans =
                                currentPlans.map { plan ->
                                    if (plan.meetingId == action.meeting.id) {
                                        plan.copy(
                                            meetingName = action.meeting.name,
                                            meetingImageUrl = action.meeting.imageUrl,
                                        )
                                    } else {
                                        plan
                                    }
                                }

                            reduce { state.copy(plans = Result.Success(plans)) }
                        }

                        is MeetingAction.MeetingDelete,
                        is MeetingAction.MeetingInvalidate,
                        -> {
                            getData()
                        }

                        else -> {
                            return@intent
                        }
                    }
                }
            }.launchIn(viewModelScope)

        planEventBus.action
            .onEach { action ->
                intent {
                    val currentPlans = state.plans.data ?: return@intent

                    when (action) {
                        is PlanAction.PlanCreate -> {
                            val plans =
                                currentPlans
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

                            reduce { state.copy(plans = Result.Success(plans)) }
                        }

                        is PlanAction.PlanUpdate -> {
                            if (currentPlans.isEmpty()) {
                                getData()
                            } else {
                                val plans =
                                    currentPlans
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
                                            it.isParticipant || it.userId == state.user.userId
                                        }

                                reduce { state.copy(plans = Result.Success(plans)) }
                            }
                        }

                        is PlanAction.PlanDelete -> {
                            val plans =
                                currentPlans.toMutableList().apply {
                                    withIndex()
                                        .firstOrNull { action.postId == it.value.planId }
                                        ?.index
                                        ?.let { index -> removeAt(index) }
                                }

                            reduce { state.copy(plans = Result.Success(plans)) }
                        }

                        is PlanAction.PlanInvalidate -> {
                            getData()
                        }

                        else -> {
                            return@intent
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    override suspend fun Syntax<HomeState, HomeSideEffect>.onContainerCreate() {
        repeatOnSubscription {
            getData()
        }
    }

    override fun onIntent(intent: Intent) {
        if (intent !is HomeIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is HomeIntent.RefreshClick -> {
                    getData()
                }

                is HomeIntent.AlarmClick -> {
                    postSideEffect(HomeSideEffect.NavigateToAlarm)
                }

                is HomeIntent.MeetingWriteClick -> {
                    postSideEffect(HomeSideEffect.NavigateToMeetingWrite)
                }

                is HomeIntent.PlanWriteClick -> {
                    if (!state.hasJoinedMeet) {
                        postSideEffect(HomeSideEffect.ShowToastMessage(ToastMessage.EmptyPlanErrorMessage))
                    } else {
                        postSideEffect(HomeSideEffect.NavigateToPlanWrite)
                    }
                }

                is HomeIntent.PlanMoreClick -> {
                    postSideEffect(HomeSideEffect.NavigateToCalendar)
                }

                is HomeIntent.PlanClick -> {
                    postSideEffect(HomeSideEffect.NavigateToPlanDetail(ViewIdType.PlanId(intent.planId)))
                }

                is HomeIntent.PermissionCheckUpdate -> {
                    reduce { state.copy(isPermissionCheck = true) }
                }
            }
        }
    }

    private fun getData() {
        intent {
            reduce { state.copy(plans = Result.Loading) }

            try {
                val user = userRepository.getUser().first()
                val meetContainer = planRepository.getCurrentPlans()

                reduce {
                    state.copy(
                        user = user,
                        plans = Result.Success(meetContainer.plans),
                        hasJoinedMeet = meetContainer.hasJoinedMeet,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                reduce { state.copy(plans = Result.Error(e)) }
            }
        }
    }
}
