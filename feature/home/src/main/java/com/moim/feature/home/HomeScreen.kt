package com.moim.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.common.view.showToast
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.Plan
import com.moim.feature.home.ui.HomeCreateCards
import com.moim.feature.home.ui.HomeMeetingMoreCard
import com.moim.feature.home.ui.HomeMeetingPlanCard
import com.moim.feature.home.ui.HomeTopAppbar
import java.time.ZonedDateTime

internal typealias OnHomeUiAction = (HomeUiAction) -> Unit

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    padding: PaddingValues,
    navigateToAlarm: () -> Unit = {},
    navigateToMeetingWrite: () -> Unit = {},
    navigateToPlanWrite: () -> Unit = {},
    navigateToCalendar: () -> Unit = {},
    navigateToMeetingDetail: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val homeUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(padding)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is HomeUiEvent.NavigateToAlarm -> navigateToAlarm()
            is HomeUiEvent.NavigateToMeetingWrite -> navigateToMeetingWrite()
            is HomeUiEvent.NavigateToPlanWrite -> navigateToPlanWrite()
            is HomeUiEvent.NavigateToCalendar -> navigateToCalendar()
            is HomeUiEvent.NavigateToMeetingDetail -> navigateToMeetingDetail(event.meetingId)
            is HomeUiEvent.ShowToastMessage -> showToast(context, event.messageRes)
        }
    }

    when (val uiState = homeUiState) {
        is HomeUiState.Loading -> LoadingScreen(modifier = modifier)

        is HomeUiState.Success -> HomeScreen(
            modifier = modifier,
            uiState = uiState,
            isLoading = isLoading,
            onUiAction = viewModel::onUiAction
        )

        is HomeUiState.Error -> ErrorScreen(
            modifier = modifier,
            onClickRefresh = { viewModel.onUiAction(HomeUiAction.OnClickRefresh) }
        )
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState.Success,
    isLoading: Boolean,
    onUiAction: OnHomeUiAction
) {
    Column(modifier = modifier) {
        HomeTopAppbar(onUiAction = onUiAction)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HomeMeetingPager(
                plans = uiState.plans,
                onUiAction = onUiAction
            )
            HomeCreateCards(
                onUiAction = onUiAction
            )
        }
    }

    LoadingDialog(isLoading)
}

@Composable
fun HomeMeetingPager(
    modifier: Modifier = Modifier,
    plans: List<Plan>,
    onUiAction: OnHomeUiAction = {}
) {
    if (plans.isEmpty()) return

    val localDensity = LocalDensity.current
    val pagerState = rememberPagerState(pageCount = { plans.size + 1 })
    var pageHeight by remember(plans) { mutableStateOf((-1).dp) }
    val heightModifier = if (pageHeight > 0.dp) Modifier.height(pageHeight) else Modifier

     HorizontalPager(
        modifier = modifier,
        state = pagerState,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 0.dp),
        pageSpacing = 8.dp,
    ) { index ->
        val meetingPlan = plans.getOrNull(index)

        if (meetingPlan != null) {
            HomeMeetingPlanCard(
                modifier = heightModifier
                    .onGloballyPositioned {
                        with(localDensity) {
                            val contentHeight = it.size.height.toDp()
                            if (pageHeight < contentHeight) pageHeight = contentHeight
                        }
                    },
                plan = meetingPlan,
                onUiAction = onUiAction
            )
        } else {
            HomeMeetingMoreCard(
                modifier = modifier.then(heightModifier),
                onUiAction = onUiAction
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    MoimTheme {
        HomeScreen(
            modifier = Modifier.containerScreen(),
            uiState = HomeUiState.Success(
                plans = listOf(
                    Plan(
                        meetingId = "1",
                        meetingName = "우리중학교 동창1",
                        planName = "술 한잔 하는 날",
                        planMemberCount = 3,
                        planAddress = "서울 강남구",
                        planTime = ZonedDateTime.now().toString()
                    ),
                    Plan(
                        meetingId = "2",
                        meetingName = "우리중학교 동창2",
                        planName = "술 한잔 하는 날",
                        planMemberCount = 3,
                        planAddress = "서울 강남구",
                        planTime = ZonedDateTime.now().toString()
                    ),
                )
            ),
            isLoading = false,
            onUiAction = {}
        )
    }
}