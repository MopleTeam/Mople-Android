package com.moim.feature.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.common.view.showToast
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.Plan
import com.moim.feature.home.ui.HomeCreateCards
import com.moim.feature.home.ui.HomePlanCard
import com.moim.feature.home.ui.HomePlanMoreCard
import com.moim.feature.home.ui.HomeTopAppbar
import kotlinx.coroutines.delay

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

    var isPostNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
        } else {
            mutableStateOf(true)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        isPostNotificationPermission = result
    }

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

    LaunchedEffect(Unit) {
        if (isPostNotificationPermission.not() && Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            delay(2000)
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
            if (uiState.plans.isEmpty()) {
                HomePlanEmpty()
            } else {
                HomePlanPager(
                    plans = uiState.plans,
                    onUiAction = onUiAction
                )
            }
            HomeCreateCards(
                onUiAction = onUiAction
            )
        }
    }

    LoadingDialog(isLoading)
}

@Composable
fun HomePlanEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .background(color = MoimTheme.colors.white, shape = RoundedCornerShape(12.dp))
            .aspectRatio(1.25f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_empty_logo),
            contentDescription = "",
            tint = Color.Unspecified
        )
        MoimText(
            text = stringResource(R.string.home_empty_plan),
            textAlign = TextAlign.Center,
            singleLine = false,
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.gray.gray06
        )
    }
}

@Composable
fun HomePlanPager(
    modifier: Modifier = Modifier,
    plans: List<Plan>,
    onUiAction: OnHomeUiAction = {}
) {
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
            HomePlanCard(
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
            HomePlanMoreCard(
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
                        planTime = "2023-12-14 09:00:00",
                    ),
                    Plan(
                        meetingId = "2",
                        meetingName = "우리중학교 동창2",
                        planName = "술 한잔 하는 날",
                        planMemberCount = 3,
                        planAddress = "서울 강남구",
                        planTime = "2023-12-15 09:00:00",
                    ),
                )
            ),
            isLoading = false,
            onUiAction = {}
        )
    }
}