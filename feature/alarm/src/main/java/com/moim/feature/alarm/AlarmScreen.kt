package com.moim.feature.alarm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.model.ViewIdType
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.PagingErrorScreen
import com.moim.core.designsystem.common.PagingLoadingScreen
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.util.decimalFormatString
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.PAGING_ERROR
import com.moim.core.ui.view.PAGING_LOADING
import com.moim.core.ui.view.isAppendError
import com.moim.core.ui.view.isAppendLoading
import com.moim.core.ui.view.isError
import com.moim.core.ui.view.isLoading
import com.moim.core.ui.view.isSuccess
import com.moim.feature.alarm.model.AlarmUiModel
import com.moim.feature.alarm.ui.AlarmEmptyScreen
import com.moim.feature.alarm.ui.AlarmListItem

@Composable
fun AlarmRoute(
    padding: PaddingValues,
    viewModel: AlarmViewModel = hiltViewModel(),
    navigateToMeetingDetail: (String) -> Unit,
    navigateToPlanDetail: (ViewIdType) -> Unit,
    navigateToBack: () -> Unit,
) {
    val notifications = viewModel.alarmItems.collectAsLazyPagingItems(LocalLifecycleOwner.current.lifecycleScope.coroutineContext)
    val totalCount by viewModel.totalCount.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.white, padding = padding)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is AlarmUiEvent.NavigateToBack -> navigateToBack()
            is AlarmUiEvent.NavigateToMeetingDetail -> navigateToMeetingDetail(event.meetingId)
            is AlarmUiEvent.NavigateToPlanDetail -> navigateToPlanDetail(event.viewIdType)
            is AlarmUiEvent.RefreshPagingData -> notifications.refresh()
        }
    }

    LaunchedEffect(notifications) {
        if (notifications.loadState.isSuccess()) {
            viewModel.onUiAction(AlarmUiAction.OnUpdateNotificationCount)
        }
    }

    AlarmScreen(
        modifier = modifier,
        totalCount = totalCount,
        alarmItems = notifications,
        onUiAction = viewModel::onUiAction
    )
}

@Composable
fun AlarmScreen(
    modifier: Modifier = Modifier,
    totalCount: Int,
    alarmItems: LazyPagingItems<AlarmUiModel>,
    onUiAction: (AlarmUiAction) -> Unit
) {
    TrackScreenViewEvent(screenName = "notification")
    Column(
        modifier = modifier
    ) {
        MoimTopAppbar(
            modifier = Modifier,
            title = stringResource(R.string.alarm_title),
            onClickNavigate = { onUiAction(AlarmUiAction.OnClickBack) }
        )

        AlarmCount(alarmCount = totalCount)

        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut(),
            visible = alarmItems.loadState.isSuccess() && alarmItems.itemCount > 0
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 64.dp)
            ) {
                items(
                    count = alarmItems.itemCount,
                    key = alarmItems.itemKey(),
                    contentType = alarmItems.itemContentType()
                ) { index ->
                    val alarmUiModel = alarmItems[index] ?: return@items
                    AlarmListItem(
                        alarm = alarmUiModel,
                        onUiAction = onUiAction
                    )
                }

                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        text = stringResource(R.string.alarm_deadline_description),
                        style = MoimTheme.typography.body01.regular,
                        color = MoimTheme.colors.gray.gray04,
                        textAlign = TextAlign.Center,
                    )
                }

                if (alarmItems.loadState.isAppendLoading()) {
                    item(key = PAGING_LOADING) {
                        PagingLoadingScreen(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .background(MoimTheme.colors.white)
                                    .animateItem(),
                        )
                    }
                }

                if (alarmItems.loadState.isAppendError()) {
                    item(key = PAGING_ERROR) {
                        PagingErrorScreen(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .background(MoimTheme.colors.white)
                                    .animateItem(),
                            onClickRetry = alarmItems::retry,
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut(),
            visible = alarmItems.loadState.isLoading()
        ) {
            PagingLoadingScreen(modifier = Modifier.fillMaxSize())
        }

        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut(),
            visible = alarmItems.loadState.isError()
        ) {
            ErrorScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MoimTheme.colors.bg.primary),
                onClickRefresh = { onUiAction(AlarmUiAction.OnClickRefresh) },
            )
        }

        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut(),
            visible = alarmItems.loadState.isSuccess() && alarmItems.itemCount == 0
        ) {
            AlarmEmptyScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MoimTheme.colors.bg.primary)
            )
        }
    }
}

@Composable
fun AlarmCount(
    modifier: Modifier = Modifier,
    alarmCount: Int,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp, bottom = 16.dp)
    ) {
        MoimText(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.alarm_new),
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.gray.gray01
        )

        MoimText(
            text = stringResource(R.string.unit_count, alarmCount.decimalFormatString()),
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.gray.gray01
        )
    }
}
