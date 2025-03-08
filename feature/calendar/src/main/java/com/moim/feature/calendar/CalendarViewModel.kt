package com.moim.feature.calendar

import androidx.lifecycle.viewModelScope
import com.kizitonwose.calendar.core.daysOfWeek
import com.moim.core.common.delegate.MeetingAction
import com.moim.core.common.delegate.MeetingViewModelDelegate
import com.moim.core.common.delegate.PlanAction
import com.moim.core.common.delegate.PlanItemViewModelDelegate
import com.moim.core.common.delegate.meetingStateIn
import com.moim.core.common.delegate.planItemStateIn
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.util.default
import com.moim.core.common.util.getDateTimeFormatZonedDate
import com.moim.core.common.util.getZonedDateTimeDefault
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.ToastMessage
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.domain.usecase.GetPlanItemForCalendarUseCase
import com.moim.core.model.item.PlanItem
import com.moim.core.model.item.asPlanItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okio.IOException
import java.time.DayOfWeek
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getPlanItemForCalendarUseCase: GetPlanItemForCalendarUseCase,
    private val planItemViewModelDelegate: PlanItemViewModelDelegate,
    private val meetingViewModelDelegate: MeetingViewModelDelegate,
) : BaseViewModel(), MeetingViewModelDelegate by meetingViewModelDelegate, PlanItemViewModelDelegate by planItemViewModelDelegate {

    private val meetingActionReceiver = meetingAction.meetingStateIn(viewModelScope)
    private val planActionReceiver = planItemAction.planItemStateIn(viewModelScope)

    private val meetingPlanResult = loadDataSignal
        .flatMapLatest { getPlanItemForCalendarUseCase(getDateTimeFormatZonedDate(pattern = "yyyyMM")).asResult() }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            launch {
                meetingPlanResult.collect { result ->
                    when (result) {
                        is Result.Loading -> setUiState(CalendarUiState.Loading)
                        is Result.Success -> setUiState(CalendarUiState.Success(plans = result.data))
                        is Result.Error -> setUiState(CalendarUiState.Error)
                    }
                }
            }

            launch {
                meetingActionReceiver.collect { action ->
                    uiState.checkState<CalendarUiState.Success> {
                        when (action) {
                            is MeetingAction.MeetingDelete -> {
                                val plan = plans.find { it.meetingId == action.meetId }
                                setUiState(copy(plans = plans.toMutableList().apply { remove(plan) }))
                            }

                            is MeetingAction.MeetingUpdate -> {
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

                                setUiState(copy(plans = plans))
                            }

                            is MeetingAction.MeetingInvalidate -> onRefresh()

                            else -> return@collect
                        }
                    }
                }
            }

            launch {
                planActionReceiver.collect { action ->
                    uiState.checkState<CalendarUiState.Success> {
                        when (action) {
                            is PlanAction.PlanCreate -> {
                                val newPlans = plans.toMutableList()
                                    .apply { add(action.planItem) }
                                    .sortedBy { it.planAt }

                                setUiState(copy(plans = newPlans))
                            }

                            is PlanAction.PlanUpdate -> {
                                val newPlan = action.planItem
                                val findIndex = plans
                                    .withIndex()
                                    .find { it.value.postId == newPlan.postId }
                                    ?.index ?: return@collect
                                val newPlans = plans
                                    .toMutableList()
                                    .apply { this[findIndex] = newPlan }
                                    .sortedBy { it.planAt }

                                setUiState(copy(plans = newPlans))
                            }

                            is PlanAction.PlanDelete -> {
                                val deletePlans = plans.toMutableList().apply { removeIf { it.postId == action.postId } }
                                setUiState(copy(plans = deletePlans))
                            }

                            is PlanAction.PlanInvalidate -> onRefresh()

                            is PlanAction.None -> return@collect
                        }
                    }
                }
            }
        }
    }

    fun onUiAction(uiAction: CalendarUiAction) {
        when (uiAction) {
            is CalendarUiAction.OnClickRefresh -> onRefresh()
            is CalendarUiAction.OnClickDateDay -> setSelectDay(uiAction.date)
            is CalendarUiAction.OnClickExpandable -> setExpandable(uiAction.date)
            is CalendarUiAction.OnClickMeetingPlan -> setUiEvent(CalendarUiEvent.NavigateToPlanDetail(uiAction.postId, uiAction.isPlan))
            is CalendarUiAction.OnChangeDate -> getSelectDatePlan(uiAction.date)
        }
    }

    private fun setSelectDay(date: ZonedDateTime) {
        uiState.checkState<CalendarUiState.Success> {
            setUiState(
                copy(
                    selectDay = if (selectDay == date) null else date,
                    isExpandable = false
                )
            )
        }
    }

    private fun setExpandable(dateTime: ZonedDateTime) {
        uiState.checkState<CalendarUiState.Success> {
            setUiState(copy(isExpandable = isExpandable.not(), selectDayOfMonth = dateTime))
        }
    }

    private fun getSelectDatePlan(date: ZonedDateTime) {
        viewModelScope.launch {
            uiState.checkState<CalendarUiState.Success> {
                if (loadDates.any { it == date }) return@launch

                getPlanItemForCalendarUseCase(
                    date = getDateTimeFormatZonedDate(dateTime = date, pattern = "yyyyMM"),
                ).asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> setUiState(
                                copy(
                                    plans = plans + result.data,
                                    loadDates = loadDates.toMutableList().apply { add(date) }
                                )
                            )

                            is Result.Error -> when (result.exception) {
                                is IOException -> setUiEvent(CalendarUiEvent.ShowToastMessage(ToastMessage.NetworkErrorMessage))
                                is NetworkException -> setUiEvent(CalendarUiEvent.ShowToastMessage(ToastMessage.ServerErrorMessage))
                            }
                        }
                    }
            }
        }
    }
}

sealed interface CalendarUiState : UiState {
    data object Loading : CalendarUiState

    data class Success(
        val plans: List<PlanItem>,
        val selectDayOfMonth: ZonedDateTime = getZonedDateTimeDefault().default().withDayOfMonth(1),
        val selectDay: ZonedDateTime? = null,
        val loadDates: List<ZonedDateTime> = listOf(selectDayOfMonth),
        val daysOfWeek: List<DayOfWeek> = daysOfWeek(),
        val isExpandable: Boolean = true,
        val isShowDatePickerDialog: Boolean = false
    ) : CalendarUiState

    data object Error : CalendarUiState
}

sealed interface CalendarUiAction : UiAction {
    data object OnClickRefresh : CalendarUiAction
    data class OnClickDateDay(val date: ZonedDateTime) : CalendarUiAction
    data class OnClickExpandable(val date: ZonedDateTime) : CalendarUiAction
    data class OnClickMeetingPlan(val postId: String, val isPlan: Boolean) : CalendarUiAction
    data class OnChangeDate(val date: ZonedDateTime) : CalendarUiAction
}

sealed interface CalendarUiEvent : UiEvent {
    data class NavigateToPlanDetail(val postId: String, val isPlan: Boolean) : CalendarUiEvent
    data class ShowToastMessage(val message: ToastMessage) : CalendarUiEvent
}