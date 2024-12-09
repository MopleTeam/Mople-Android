package com.moim.feature.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.WeekDayPosition
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.yearMonth
import com.moim.core.common.util.default
import com.moim.core.common.util.parseZonedDateTime
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.common.view.showToast
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.calendar.ui.CalendarDay
import com.moim.feature.calendar.ui.CalendarDayOfWeekHeader
import com.moim.feature.calendar.ui.CalendarMonthCard
import com.moim.feature.calendar.ui.CalendarPlanContent
import com.moim.feature.calendar.ui.CalendarTopAppbar
import com.moim.feature.calendar.util.rememberFirstMostVisibleMonth
import com.moim.feature.calendar.util.rememberFirstVisibleWeekAfterScroll
import java.time.LocalDate
import java.time.ZonedDateTime

internal typealias OnCalendarUiAction = (CalendarUiAction) -> Unit

@Composable
fun CalendarRoute(
    viewModel: CalendarViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToPlanDetail: (String) -> Unit
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val calendarUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.white)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is CalendarUiEvent.NavigateToPlanDetail -> navigateToPlanDetail(event.planId)
            is CalendarUiEvent.ShowToastMessage -> showToast(context, event.messageRes)
        }
    }

    when (val uiState = calendarUiState) {
        is CalendarUiState.Loading -> LoadingScreen(modifier)

        is CalendarUiState.Success -> CalendarScreen(
            modifier = modifier,
            uiState = uiState,
            isLoading = isLoading,
            onUiAction = viewModel::onUiAction
        )

        is CalendarUiState.Error -> ErrorScreen(
            modifier = modifier,
            onClickRefresh = { viewModel.onUiAction(CalendarUiAction.OnClickRefresh) }
        )
    }
}

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    uiState: CalendarUiState.Success,
    isLoading: Boolean,
    onUiAction: OnCalendarUiAction
) {
    val localDate = uiState.selectDayOfMonth.toLocalDate()
    val startDate = localDate.yearMonth.minusMonths(500)
    val endDate = localDate.yearMonth.plusMonths(500)

    val monthState = rememberCalendarState(
        startMonth = startDate,
        endMonth = endDate,
        firstVisibleMonth = localDate.yearMonth,
        firstDayOfWeek = uiState.daysOfWeek.first(),
    )
    val weekState = rememberWeekCalendarState(
        startDate = startDate.atStartOfMonth(),
        endDate = endDate.atEndOfMonth(),
        firstVisibleWeekDate = uiState.selectDay?.toLocalDate() ?: LocalDate.now(),
        firstDayOfWeek = uiState.daysOfWeek.first(),
    )
    val currentDateForWeek = rememberFirstVisibleWeekAfterScroll(state = weekState)
    val currentDate = rememberFirstMostVisibleMonth(state = monthState, viewportPercent = 90f)
        .yearMonth
        .atStartOfMonth()
        .parseZonedDateTime()

    LaunchedEffect(currentDateForWeek) {
        val weekDate = currentDateForWeek.days.first().date
            .parseZonedDateTime()
            .default()
            .withDayOfMonth(1)

        if (uiState.selectDayOfMonth == weekDate) return@LaunchedEffect
        onUiAction(CalendarUiAction.OnChangeDate(weekDate))
    }

    LaunchedEffect(currentDate) {
        onUiAction(CalendarUiAction.OnChangeDate(currentDate))
    }

    Column(
        modifier = modifier
    ) {
        CalendarTopAppbar(
            currentDate = currentDate,
            onUiAction = onUiAction
        )
        AnimatedVisibility(visible = uiState.isExpandable) {
            CalendarMonth(
                uiState = uiState,
                currentDate = currentDate,
                monthState = monthState,
                onUiAction = onUiAction
            )
        }

        AnimatedVisibility(visible = uiState.isExpandable.not()) {
            CalendarWeek(
                uiState = uiState,
                weekState = weekState,
                onUiAction = onUiAction
            )
        }
    }

    LoadingDialog(isLoading)
}

@Composable
fun CalendarMonth(
    modifier: Modifier = Modifier,
    uiState: CalendarUiState.Success,
    currentDate: ZonedDateTime,
    monthState: CalendarState,
    onUiAction: OnCalendarUiAction
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        CalendarMonthCard(selectDate = currentDate)
        Spacer(Modifier.height(16.dp))

        CalendarDayOfWeekHeader(
            daysOfWeek = uiState.daysOfWeek
        )
        HorizontalCalendar(
            modifier = Modifier.fillMaxSize(),
            state = monthState,
            dayContent = { day ->
                val dayForZonedDateTime = day.date.parseZonedDateTime().default()
                val enabled = uiState.plans.find { it.planTime.parseZonedDateTime().default() == dayForZonedDateTime } != null

                CalendarDay(
                    day = dayForZonedDateTime,
                    selectedDay = uiState.selectDay,
                    isCurrentDatePosition = day.position == DayPosition.MonthDate,
                    enabled = enabled,
                    onUiAction = onUiAction
                )
            }
        )
    }
}

@Composable
fun CalendarWeek(
    modifier: Modifier = Modifier,
    uiState: CalendarUiState.Success,
    weekState: WeekCalendarState,
    onUiAction: OnCalendarUiAction
) {
    val selectedDatePlans = uiState.plans.filter {
        it.planTime.parseZonedDateTime().dayOfMonth == (uiState.selectDay ?: ZonedDateTime.now()).dayOfMonth
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        CalendarDayOfWeekHeader(
            daysOfWeek = uiState.daysOfWeek,
            selectedDayOfWeek = uiState.selectDay?.dayOfWeek
        )
        WeekCalendar(
            state = weekState,
            dayContent = { day ->
                val dayForZonedDateTime = day.date.parseZonedDateTime().default()
                val enabled = uiState.plans.find { it.planTime.parseZonedDateTime().default() == dayForZonedDateTime } != null

                CalendarDay(
                    day = dayForZonedDateTime,
                    selectedDay = uiState.selectDay,
                    isCurrentDatePosition = day.position == WeekDayPosition.RangeDate,
                    enabled = enabled,
                    onUiAction = onUiAction
                )
            },
        )

        if (selectedDatePlans.isNotEmpty()) {
            CalendarPlanContent(
                selectDate = uiState.selectDay ?: ZonedDateTime.now().default(),
                plans = selectedDatePlans,
                onUiAction = onUiAction
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MoimTheme.colors.bg.primary),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_empty_calendar),
                    contentDescription = "",
                    tint = MoimTheme.colors.icon
                )

                MoimText(
                    text = stringResource(R.string.calendar_empty),
                    textAlign = TextAlign.Center,
                    style = MoimTheme.typography.title03.medium,
                    color = MoimTheme.colors.gray.gray06,
                )
            }
        }
    }
}