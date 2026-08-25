package com.moim.feature.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kizitonwose.calendar.compose.CalendarState as MonthCalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.WeekCalendarState
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.WeekDayPosition
import com.kizitonwose.calendar.core.atStartOfMonth
import com.kizitonwose.calendar.core.yearMonth
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.result.data
import com.moim.core.common.util.default
import com.moim.core.common.util.parseZonedDateTime
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.view.showToast
import com.moim.feature.calendar.model.CalendarIntent
import com.moim.feature.calendar.model.CalendarSideEffect
import com.moim.feature.calendar.model.CalendarState
import com.moim.feature.calendar.ui.CalendarDay
import com.moim.feature.calendar.ui.CalendarDayOfWeekHeader
import com.moim.feature.calendar.ui.CalendarMonthCard
import com.moim.feature.calendar.ui.CalendarPlanContent
import com.moim.feature.calendar.ui.CalendarTopAppbar
import com.moim.feature.calendar.util.rememberFirstMostVisibleMonth
import com.moim.feature.calendar.util.rememberFirstVisibleWeekAfterScroll
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import java.time.LocalDate
import java.time.ZonedDateTime

internal typealias OnCalendarIntent = (CalendarIntent) -> Unit

@Composable
fun CalendarRoute(
    viewModel: CalendarViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToPlanDetail: (ViewIdType) -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val calendarUiState by viewModel.collectAsState()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary)

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is CalendarSideEffect.NavigateToPlanDetail -> navigateToPlanDetail(sideEffect.viewIdType)
            is CalendarSideEffect.ShowToastMessage -> showToast(context, sideEffect.message)
        }
    }

    when {
        calendarUiState.isLoading -> {
            LoadingScreen(modifier)
        }

        calendarUiState.isSuccess -> {
            CalendarScreen(
                modifier = modifier,
                uiState = calendarUiState,
                isLoading = isLoading,
                onIntent = viewModel::onIntent,
            )
        }

        calendarUiState.isError -> {
            ErrorScreen(
                modifier = modifier,
                onClickRefresh = { viewModel.onIntent(CalendarIntent.RefreshClick) },
            )
        }
    }
}

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    uiState: CalendarState,
    isLoading: Boolean,
    onIntent: OnCalendarIntent,
) {
    val plans = uiState.plans.data.orEmpty()
    val localDate = uiState.selectDayOfMonth.toLocalDate()
    val startDate = localDate.yearMonth.minusMonths(500)
    val endDate = localDate.yearMonth.plusMonths(500)

    val monthState =
        rememberCalendarState(
            startMonth = startDate,
            endMonth = endDate,
            firstVisibleMonth = localDate.yearMonth,
            firstDayOfWeek = uiState.daysOfWeek.first(),
        )
    val weekState =
        rememberWeekCalendarState(
            startDate = startDate.atStartOfMonth(),
            endDate = endDate.atEndOfMonth(),
            firstVisibleWeekDate = uiState.selectDay?.toLocalDate() ?: LocalDate.now(),
            firstDayOfWeek = uiState.daysOfWeek.first(),
        )
    val currentDateForWeek = rememberFirstVisibleWeekAfterScroll(state = weekState)
    val currentDate =
        rememberFirstMostVisibleMonth(state = monthState, viewportPercent = 90f)
            .yearMonth
            .atStartOfMonth()
            .parseZonedDateTime()

    LaunchedEffect(currentDateForWeek) {
        val weekDate =
            currentDateForWeek.days
                .first()
                .date
                .parseZonedDateTime()
                .default()
                .withDayOfMonth(1)

        if (uiState.selectDayOfMonth == weekDate) return@LaunchedEffect
        onIntent(CalendarIntent.DateChange(weekDate))
    }

    LaunchedEffect(currentDate) {
        onIntent(CalendarIntent.DateChange(currentDate))
    }

    TrackScreenViewEvent(screenName = "calendar")
    Column(
        modifier = modifier,
    ) {
        CalendarTopAppbar(
            currentDate = currentDate,
            onIntent = onIntent,
        )
        AnimatedVisibility(
            visible = uiState.isExpandable,
        ) {
            CalendarMonth(
                uiState = uiState,
                plans = plans,
                currentDate = currentDate,
                monthState = monthState,
                onIntent = onIntent,
            )
        }

        AnimatedVisibility(
            enter = fadeIn(),
            exit = fadeOut(),
            visible = uiState.isExpandable.not(),
        ) {
            CalendarWeek(
                uiState = uiState,
                plans = plans,
                weekState = weekState,
                onIntent = onIntent,
            )
        }
    }

    LoadingDialog(isLoading)
}

@Composable
fun CalendarMonth(
    modifier: Modifier = Modifier,
    uiState: CalendarState,
    plans: List<PlanItem>,
    currentDate: ZonedDateTime,
    monthState: MonthCalendarState,
    onIntent: OnCalendarIntent,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        CalendarMonthCard(selectDate = currentDate)
        Spacer(Modifier.height(16.dp))

        CalendarDayOfWeekHeader(
            daysOfWeek = uiState.daysOfWeek,
        )
        HorizontalCalendar(
            modifier = Modifier.fillMaxSize(),
            state = monthState,
            dayContent = { day ->
                val dayForZonedDateTime = day.date.parseZonedDateTime().default()
                val enabled = plans.find { it.planAt.default() == dayForZonedDateTime } != null

                CalendarDay(
                    day = dayForZonedDateTime,
                    selectedDay = uiState.selectDay,
                    holidays = uiState.holidays.filter { it.year == day.date.year && it.month == day.date.month },
                    isCurrentDatePosition = day.position == DayPosition.MonthDate,
                    enabled = enabled,
                    onIntent = onIntent,
                )
            },
        )
    }
}

@Composable
fun CalendarWeek(
    modifier: Modifier = Modifier,
    uiState: CalendarState,
    plans: List<PlanItem>,
    weekState: WeekCalendarState,
    onIntent: OnCalendarIntent,
) {
    val selectedDatePlans =
        plans.filter {
            it.planAt.dayOfMonth == (uiState.selectDay ?: ZonedDateTime.now()).dayOfMonth
        }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        CalendarDayOfWeekHeader(
            daysOfWeek = uiState.daysOfWeek,
            selectedDayOfWeek = uiState.selectDay?.dayOfWeek,
        )
        WeekCalendar(
            state = weekState,
            dayContent = { day ->
                val dayForZonedDateTime = day.date.parseZonedDateTime().default()
                val enabled = plans.find { it.planAt.default() == dayForZonedDateTime } != null

                CalendarDay(
                    day = dayForZonedDateTime,
                    selectedDay = uiState.selectDay,
                    holidays = uiState.holidays.filter { it.year == day.date.year && it.month == day.date.month },
                    isCurrentDatePosition = day.position == WeekDayPosition.RangeDate,
                    enabled = enabled,
                    onIntent = onIntent,
                )
            },
        )

        if (selectedDatePlans.isNotEmpty()) {
            CalendarPlanContent(
                selectDate = uiState.selectDay ?: ZonedDateTime.now().default(),
                plans = selectedDatePlans,
                onIntent = onIntent,
            )
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(MoimTheme.colors.bg.primary),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_empty_calendar),
                    contentDescription = "",
                    tint = MoimTheme.colors.icon,
                )

                MoimText(
                    text = stringResource(R.string.calendar_empty),
                    textAlign = TextAlign.Center,
                    style = MoimTheme.typography.title03.medium,
                    color = MoimTheme.colors.text.text04,
                )
            }
        }
    }
}
