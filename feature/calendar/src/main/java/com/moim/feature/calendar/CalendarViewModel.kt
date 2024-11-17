package com.moim.feature.calendar

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.kizitonwose.calendar.core.daysOfWeek
import com.moim.core.common.exception.NetworkException
import com.moim.core.common.result.Result
import com.moim.core.common.result.asResult
import com.moim.core.common.util.default
import com.moim.core.common.util.getDateTimeFormatZoneDate
import com.moim.core.common.util.getZonedDateTimeDefault
import com.moim.core.common.view.BaseViewModel
import com.moim.core.common.view.UiAction
import com.moim.core.common.view.UiEvent
import com.moim.core.common.view.UiState
import com.moim.core.common.view.checkState
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.model.MeetingPlanResponse
import com.moim.core.designsystem.R
import com.moim.core.model.MeetingPlan
import com.moim.core.model.Participant
import com.moim.core.model.asItem
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
    private val planRepository: PlanRepository
) : BaseViewModel() {

    private val meetingPlanResult = loadDataSignal
        .flatMapLatest {
            planRepository
                .getPlans(
                    page = 1,
                    yearAndMonth = getDateTimeFormatZoneDate(pattern = "yyyyMM"),
                    isClosed = false
                )
                .asResult()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, Result.Loading)

    init {
        viewModelScope.launch {
            meetingPlanResult.collect { result ->
                when (result) {
                    is Result.Loading -> setUiState(CalendarUiState.Loading)
                    is Result.Success -> setUiState(CalendarUiState.Success(plans = result.data.map(MeetingPlanResponse::asItem) + sample))
                    is Result.Error -> setUiState(CalendarUiState.Error)
                }
            }
        }
    }

    fun onUiAction(uiAction: CalendarUiAction) {
        when (uiAction) {
            is CalendarUiAction.OnClickDateDay -> setSelectDay(uiAction.date)
            is CalendarUiAction.OnClickExpandable -> setExpandable(uiAction.date)
            is CalendarUiAction.OnClickMeetingPlan -> setUiEvent(CalendarUiEvent.NavigateToPlanDetail(uiAction.id))
            is CalendarUiAction.OnClickRefresh -> onRefresh()
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
                planRepository
                    .getPlans(
                        page = 1,
                        yearAndMonth = getDateTimeFormatZoneDate(dateTime = date, pattern = "yyyyMM"),
                        isClosed = false
                    )
                    .asResult()
                    .onEach { setLoading(it is Result.Loading) }
                    .collect { result ->
                        when (result) {
                            is Result.Loading -> return@collect
                            is Result.Success -> setUiState(
                                copy(
                                    plans = plans + result.data.map(MeetingPlanResponse::asItem),
                                    loadDates = loadDates.toMutableList().apply { add(date) }
                                )
                            )

                            is Result.Error -> when (result.exception) {
                                is IOException -> setUiEvent(CalendarUiEvent.ShowToastMessage(R.string.common_error))
                                is NetworkException -> setUiEvent(CalendarUiEvent.ShowToastMessage(R.string.common_error))
                            }
                        }
                    }
            }
        }
    }

    companion object {
        private val sample = listOf(
            MeetingPlan(
                id = "1",
                name = "술 한 잔 하는 날",
                meetingName = "우리중학교동창",
                temperature = 16.2f,
                participants = listOf(Participant(), Participant(), Participant()),
                startedAt = ZonedDateTime.now().toString()
            ),
            MeetingPlan(
                id = "2",
                name = "술 한 잔 하는 날",
                meetingName = "우리중학교동창",
                temperature = 12.0f,
                participants = listOf(Participant(), Participant(), Participant()),
                startedAt = ZonedDateTime.now().plusDays(1).toString()
            ),
            MeetingPlan(
                id = "3",
                name = "술 한 잔 하는 날",
                meetingName = "우리중학교동창",
                temperature = 15.2f,
                participants = listOf(Participant(), Participant(), Participant()),
                startedAt = ZonedDateTime.now().plusDays(2).toString()
            ),
        )
    }
}

sealed interface CalendarUiState : UiState {
    data object Loading : CalendarUiState

    data class Success(
        val plans: List<MeetingPlan>,
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
    data class OnClickDateDay(val date: ZonedDateTime) : CalendarUiAction
    data class OnClickExpandable(val date: ZonedDateTime) : CalendarUiAction
    data class OnClickMeetingPlan(val id: String) : CalendarUiAction
    data object OnClickRefresh : CalendarUiAction
    data class OnChangeDate(val date: ZonedDateTime) : CalendarUiAction
}

sealed interface CalendarUiEvent : UiEvent {
    data class NavigateToPlanDetail(val planId: String) : CalendarUiEvent
    data class ShowToastMessage(@StringRes val messageRes: Int) : CalendarUiEvent
}