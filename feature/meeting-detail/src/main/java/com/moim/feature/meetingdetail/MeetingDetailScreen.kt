package com.moim.feature.meetingdetail

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.model.item.asPlanItem
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.common.PagingLoadingScreen
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.component.MoimFloatingActionButton
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.core.ui.util.externalShareForUrl
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.isError
import com.moim.core.ui.view.isLoading
import com.moim.core.ui.view.isSuccess
import com.moim.core.ui.view.showToast
import com.moim.feature.meetingdetail.ui.MeetingDetailHeader
import com.moim.feature.meetingdetail.ui.MeetingDetailPlanContent
import com.moim.feature.meetingdetail.ui.MeetingDetailPlanEmpty

@Composable
fun MeetingDetailRoute(
    padding: PaddingValues,
    viewModel: MeetingDetailViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToPlanWrite: (PlanItem) -> Unit,
    navigateToPlanDetail: (ViewIdType) -> Unit,
    navigateToMeetingSetting: (Meeting) -> Unit,
    navigateToImageViewer: (title: String, images: List<String>, position: Int, defaultImage: Int) -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val meetingDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.white)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is MeetingDetailUiEvent.NavigateToBack -> navigateToBack()
            is MeetingDetailUiEvent.NavigateToMeetingSetting -> navigateToMeetingSetting(event.meeting)
            is MeetingDetailUiEvent.NavigateToPlanDetail -> navigateToPlanDetail(event.viewIdType)
            is MeetingDetailUiEvent.NavigateToPlanWrite -> navigateToPlanWrite(event.plan.asPlanItem())
            is MeetingDetailUiEvent.NavigateToImageViewer -> navigateToImageViewer(event.meetingName, listOf(event.imageUrl), 0, R.drawable.ic_empty_meeting)
            is MeetingDetailUiEvent.NavigateToExternalShareUrl -> context.externalShareForUrl(event.url)
            is MeetingDetailUiEvent.ShowToastMessage -> showToast(context, event.message)
        }
    }

    when (val uiState = meetingDetailUiState) {
        is MeetingDetailUiState.Loading -> LoadingScreen(modifier)

        is MeetingDetailUiState.Success -> {
            MeetingDetailScreen(
                modifier = modifier,
                uiState = uiState,
                isLoading = isLoading,
                onUiAction = viewModel::onUiAction
            )
        }

        is MeetingDetailUiState.Error -> ErrorScreen(
            modifier = modifier,
            onClickRefresh = { viewModel.onUiAction(MeetingDetailUiAction.OnClickRefresh) }
        )
    }
}

@Composable
fun MeetingDetailScreen(
    modifier: Modifier = Modifier,
    uiState: MeetingDetailUiState.Success,
    isLoading: Boolean = false,
    onUiAction: (MeetingDetailUiAction) -> Unit,
) {
    val plans = uiState.plans?.collectAsLazyPagingItems(LocalLifecycleOwner.current.lifecycleScope.coroutineContext)
    val reviews = uiState.reviews?.collectAsLazyPagingItems(LocalLifecycleOwner.current.lifecycleScope.coroutineContext)

    TrackScreenViewEvent(screenName = "meet_detail")
    Column(
        modifier = modifier
    ) {
        MoimTopAppbar(
            actions = {
                MoimIconButton(
                    iconRes = R.drawable.ic_burger,
                    onClick = { onUiAction(MeetingDetailUiAction.OnClickMeetingSetting) }
                )
            },
            onClickNavigate = { onUiAction(MeetingDetailUiAction.OnClickBack) }
        )

        MeetingDetailHeader(
            meeting = uiState.meeting,
            isSelectedFuturePlan = uiState.isPlanSelected,
            onUiAction = onUiAction
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MoimTheme.colors.bg.primary)
        ) {
            if (plans == null || reviews == null) return

            androidx.compose.animation.AnimatedVisibility(
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(),
                exit = fadeOut(),
                visible = plans.loadState.isSuccess() && reviews.loadState.isSuccess()
            ) {
                MeetingDetailPlanContent(
                    userId = uiState.userId,
                    plans = plans,
                    reviews = reviews,
                    isPlanSelected = uiState.isPlanSelected,
                    planTotalCount = uiState.planTotalCount,
                    reviewTotalCount = uiState.reviewTotalCount,
                    onUiAction = onUiAction
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                enter = fadeIn(),
                exit = fadeOut(),
                visible = plans.loadState.isLoading() || reviews.loadState.isLoading(),
            ) {
                PagingLoadingScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(),
                exit = fadeOut(),
                visible = plans.loadState.isError() || reviews.loadState.isError()
            ) {
                ErrorScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MoimTheme.colors.bg.primary),
                    onClickRefresh = { onUiAction(MeetingDetailUiAction.OnClickRefresh) },
                )
            }


            androidx.compose.animation.AnimatedVisibility(
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(),
                exit = fadeOut(),
                visible = uiState.isPlanSelected && plans.loadState.isSuccess() && plans.itemCount == 0
            ) {
                MeetingDetailPlanEmpty()
            }

            androidx.compose.animation.AnimatedVisibility(
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(),
                exit = fadeOut(),
                visible = !uiState.isPlanSelected && reviews.loadState.isSuccess() && reviews.itemCount == 0
            ) {
                MeetingDetailPlanEmpty()
            }

            MoimFloatingActionButton(
                modifier = Modifier
                    .padding(end = 24.dp, bottom = 20.dp)
                    .size(54.dp)
                    .align(Alignment.BottomEnd),
                onClick = { onUiAction(MeetingDetailUiAction.OnClickPlanWrite) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = ""
                )
            }
        }
    }

    if (uiState.isShowApplyCancelDialog) {
        val dismissAction = MeetingDetailUiAction.OnShowPlanApplyCancelDialog(false, null)

        MoimAlertDialog(
            title = stringResource(R.string.meeting_detail_plan_cancel),
            positiveButtonColors = moimButtomColors().copy(containerColor = MoimTheme.colors.secondary),
            onDismiss = { onUiAction(dismissAction) },
            onClickNegative = { onUiAction(dismissAction) },
            onClickPositive = {
                if (uiState.cancelPlanItem == null) return@MoimAlertDialog
                onUiAction(MeetingDetailUiAction.OnClickPlanApply(uiState.cancelPlanItem, false))
            }
        )
    }

    LoadingDialog(isLoading)
}
