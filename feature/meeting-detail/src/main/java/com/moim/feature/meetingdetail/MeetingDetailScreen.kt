package com.moim.feature.meetingdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.analytics.TrackScreenViewEvent
import com.moim.core.common.model.Meeting
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.model.item.asPlanItem
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.component.MoimFloatingActionButton
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.core.ui.util.externalShareForUrl
import com.moim.core.ui.view.ObserveAsEvents
import com.moim.core.ui.view.showToast
import com.moim.feature.meetingdetail.ui.MeetingDetailHeader
import com.moim.feature.meetingdetail.ui.MeetingDetailPlanContent
import com.moim.feature.meetingdetail.ui.MeetingDetailTopAppbar

@Composable
fun MeetingDetailRoute(
    padding: PaddingValues,
    viewModel: MeetingDetailViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToPlanWrite: (PlanItem) -> Unit,
    navigateToPlanDetail: (ViewIdType) -> Unit,
    navigateToMeetingSetting: (Meeting) -> Unit,
    navigateToMeetingNotice: (meetId: String) -> Unit,
    navigateToImageViewer: (title: String, images: List<String>, position: Int, defaultImage: Int) -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val meetingDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is MeetingDetailUiEvent.NavigateToBack -> {
                navigateToBack()
            }

            is MeetingDetailUiEvent.NavigateToMeetingSetting -> {
                navigateToMeetingSetting(event.meeting)
            }

            is MeetingDetailUiEvent.NavigateToMeetingNotice -> {
                navigateToMeetingNotice(event.meetId)
            }

            is MeetingDetailUiEvent.NavigateToPlanDetail -> {
                navigateToPlanDetail(event.viewIdType)
            }

            is MeetingDetailUiEvent.NavigateToPlanWrite -> {
                navigateToPlanWrite(event.plan.asPlanItem())
            }

            is MeetingDetailUiEvent.NavigateToImageViewer -> {
                navigateToImageViewer(
                    event.meetingName,
                    listOf(event.imageUrl),
                    0,
                    R.drawable.ic_empty_meeting,
                )
            }

            is MeetingDetailUiEvent.NavigateToExternalShareUrl -> {
                context.externalShareForUrl(event.url)
            }

            is MeetingDetailUiEvent.ShowToastMessage -> {
                showToast(context, event.message)
            }
        }
    }

    when (val uiState = meetingDetailUiState) {
        is MeetingDetailUiState.Loading -> {
            LoadingScreen(modifier)
        }

        is MeetingDetailUiState.Success -> {
            MeetingDetailScreen(
                modifier = modifier,
                uiState = uiState,
                isLoading = isLoading,
                onUiAction = viewModel::onUiAction,
            )
        }

        is MeetingDetailUiState.Error -> {
            ErrorScreen(
                modifier = modifier,
                onClickRefresh = { viewModel.onUiAction(MeetingDetailUiAction.OnClickRefresh) },
            )
        }
    }
}

@Composable
fun MeetingDetailScreen(
    modifier: Modifier = Modifier,
    uiState: MeetingDetailUiState.Success,
    isLoading: Boolean = false,
    onUiAction: (MeetingDetailUiAction) -> Unit,
) {
    TrackScreenViewEvent(screenName = "meet_detail")
    Column(
        modifier = modifier,
    ) {
        MeetingDetailTopAppbar(
            meeting = uiState.meeting,
            onUiAction = onUiAction,
        )

        MeetingDetailHeader(
            isSelectedFuturePlan = uiState.isPlanSelected,
            onUiAction = onUiAction,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MoimTheme.colors.bg.secondary),
        ) {
            MeetingDetailPlanContent(
                userId = uiState.userId,
                isPlanSelected = uiState.isPlanSelected,
                plans = uiState.plans,
                reviews = uiState.reviews,
                plansPagingInfo = uiState.plansPagingInfo,
                reviewsPagingInfo = uiState.reviewsPagingInfo,
                planTotalCount = uiState.planTotalCount,
                reviewTotalCount = uiState.reviewTotalCount,
                onUiAction = onUiAction,
            )

            MoimFloatingActionButton(
                modifier =
                    Modifier
                        .padding(end = 24.dp, bottom = 20.dp)
                        .size(54.dp)
                        .align(Alignment.BottomEnd),
                onClick = { onUiAction(MeetingDetailUiAction.OnClickPlanWrite) },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_add),
                    contentDescription = "",
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
            },
        )
    }

    LoadingDialog(isLoading)
}
