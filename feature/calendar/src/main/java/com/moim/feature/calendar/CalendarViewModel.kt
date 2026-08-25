package com.moim.feature.calendar

import androidx.lifecycle.viewModelScope
import com.moim.core.common.result.Result
import com.moim.core.common.result.data
import com.moim.core.common.util.parseDateString
import com.moim.core.data.datasource.holiday.HolidayRepository
import com.moim.core.domain.usecase.GetPlanItemForCalendarUseCase
import com.moim.core.ui.eventbus.EventBus
import com.moim.core.ui.eventbus.MeetingAction
import com.moim.core.ui.eventbus.PlanAction
import com.moim.core.ui.mvi.Intent
import com.moim.core.ui.mvi.MVIViewModel
import com.moim.core.ui.view.ToastMessage
import com.moim.feature.calendar.model.CalendarIntent
import com.moim.feature.calendar.model.CalendarSideEffect
import com.moim.feature.calendar.model.CalendarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.syntax.Syntax
import java.io.IOException
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val holidayRepository: HolidayRepository,
    private val getPlanItemForCalendarUseCase: GetPlanItemForCalendarUseCase,
    planEventBus: EventBus<PlanAction>,
    meetingEventBus: EventBus<MeetingAction>,
) : MVIViewModel<CalendarState, CalendarSideEffect>(CalendarState()) {
    init {
        meetingEventBus.action
            .onEach { action ->
                intent {
                    val currentPlans = state.plans.data ?: return@intent

                    when (action) {
                        is MeetingAction.MeetingDelete -> {
                            val plans = currentPlans.filterNot { it.meetingId == action.meetId }

                            reduce { state.copy(plans = Result.Success(plans)) }
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

                        is MeetingAction.MeetingInvalidate -> {
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
                            val plans = (currentPlans + action.planItem).sortedBy { it.planAt }

                            reduce { state.copy(plans = Result.Success(plans)) }
                        }

                        is PlanAction.PlanUpdate -> {
                            val newPlan = action.planItem
                            val findIndex =
                                currentPlans
                                    .withIndex()
                                    .find { it.value.postId == newPlan.postId }
                                    ?.index ?: return@intent
                            val plans =
                                currentPlans
                                    .toMutableList()
                                    .apply { this[findIndex] = newPlan }
                                    .sortedBy { it.planAt }

                            reduce { state.copy(plans = Result.Success(plans)) }
                        }

                        is PlanAction.PlanDelete -> {
                            val plans = currentPlans.filterNot { it.postId == action.postId }

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

    override suspend fun Syntax<CalendarState, CalendarSideEffect>.onContainerCreate() {
        getData()
    }

    override fun onIntent(intent: Intent) {
        if (intent !is CalendarIntent) {
            super.onIntent(intent)
            return
        }

        intent {
            when (intent) {
                is CalendarIntent.RefreshClick -> {
                    getData()
                }

                is CalendarIntent.DateDayClick -> {
                    if (state.selectDay == intent.date) return@intent

                    reduce { state.copy(selectDay = intent.date, isExpandable = false) }
                }

                is CalendarIntent.ExpandableClick -> {
                    reduce {
                        state.copy(
                            isExpandable = state.isExpandable.not(),
                            selectDayOfMonth = intent.date,
                        )
                    }
                }

                is CalendarIntent.MeetingPlanClick -> {
                    postSideEffect(CalendarSideEffect.NavigateToPlanDetail(intent.viewIdType))
                }

                is CalendarIntent.DateChange -> {
                    getSelectDatePlan(intent.date)
                }
            }
        }
    }

    private fun getData() {
        intent {
            reduce { state.copy(plans = Result.Loading) }

            try {
                val date = ZonedDateTime.now()
                val (holidays, plans) = fetchCalendar(date)

                // 갱신 시 선택 날짜/로드된 월 정보도 초기화
                reduce {
                    CalendarState(
                        plans = Result.Success(plans),
                        holidays = holidays,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                reduce { state.copy(plans = Result.Error(e)) }
            }
        }
    }

    private fun getSelectDatePlan(date: ZonedDateTime) {
        intent {
            if (!state.isSuccess) return@intent
            if (state.loadDates.any { it == date }) return@intent

            val isHolidayFetch = state.loadDates.any { it.year != date.year }

            setLoading(true)

            try {
                val (newHolidays, newPlans) = fetchCalendar(date, isHolidayFetch)

                reduce {
                    state.copy(
                        plans = Result.Success(state.plans.data.orEmpty() + newPlans),
                        holidays = state.holidays + newHolidays,
                        loadDates = state.loadDates + date,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = if (e is IOException) ToastMessage.NetworkErrorMessage else ToastMessage.ServerErrorMessage
                postSideEffect(CalendarSideEffect.ShowToastMessage(message))
            } finally {
                setLoading(false)
            }
        }
    }

    private suspend fun fetchCalendar(
        date: ZonedDateTime,
        isHolidayFetch: Boolean = true,
    ) = coroutineScope {
        val holidays =
            async {
                if (isHolidayFetch) holidayRepository.getHolidays(date).map { it.date } else emptyList()
            }
        val plans = async { getPlanItemForCalendarUseCase(date.parseDateString("yyyyMM")) }

        holidays.await() to plans.await()
    }
}
