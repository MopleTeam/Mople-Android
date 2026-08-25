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
import com.moim.core.common.model.NoticeType
import com.moim.core.common.model.ViewIdType
import com.moim.core.common.model.item.PlanItem
import com.moim.core.common.model.item.asPlanItem
import com.moim.core.common.result.Result
import com.moim.core.common.result.data
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimAlertDialog
import com.moim.core.designsystem.component.MoimFloatingActionButton
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimButtomColors
import com.moim.core.ui.util.externalShareForUrl
import com.moim.core.ui.view.showToast
import com.moim.feature.meetingdetail.model.MeetingDetailIntent
import com.moim.feature.meetingdetail.model.MeetingDetailNoticeUiModel
import com.moim.feature.meetingdetail.model.MeetingDetailSideEffect
import com.moim.feature.meetingdetail.model.MeetingDetailState
import com.moim.feature.meetingdetail.ui.MeetingDetailHeader
import com.moim.feature.meetingdetail.ui.MeetingDetailNotice
import com.moim.feature.meetingdetail.ui.MeetingDetailPlanContent
import com.moim.feature.meetingdetail.ui.MeetingDetailTopAppbar
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MeetingDetailRoute(
    padding: PaddingValues,
    viewModel: MeetingDetailViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToPlanWrite: (PlanItem) -> Unit,
    navigateToPlanDetail: (ViewIdType) -> Unit,
    navigateToMeetingSetting: (Meeting) -> Unit,
    navigateToMeetingNotice: (meetId: String) -> Unit,
    navigateToMeetingNoticeDetail: (meetId: String, noticeId: String) -> Unit,
    navigateToImageViewer: (title: String, images: List<String>, position: Int, defaultImage: Int) -> Unit,
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val uiState by viewModel.collectAsState()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.bg.primary)

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MeetingDetailSideEffect.NavigateToBack -> {
                navigateToBack()
            }

            is MeetingDetailSideEffect.NavigateToMeetingSetting -> {
                navigateToMeetingSetting(sideEffect.meeting)
            }

            is MeetingDetailSideEffect.NavigateToMeetingNotice -> {
                navigateToMeetingNotice(sideEffect.meetId)
            }

            is MeetingDetailSideEffect.NavigateToMeetingNoticeDetail -> {
                navigateToMeetingNoticeDetail(sideEffect.meetId, sideEffect.noticeId)
            }

            is MeetingDetailSideEffect.NavigateToPlanDetail -> {
                navigateToPlanDetail(sideEffect.viewIdType)
            }

            is MeetingDetailSideEffect.NavigateToPlanWrite -> {
                navigateToPlanWrite(sideEffect.plan.asPlanItem())
            }

            is MeetingDetailSideEffect.NavigateToImageViewer -> {
                navigateToImageViewer(
                    sideEffect.meetingName,
                    listOf(sideEffect.imageUrl),
                    0,
                    R.drawable.ic_empty_meeting,
                )
            }

            is MeetingDetailSideEffect.NavigateToExternalShareUrl -> {
                context.externalShareForUrl(sideEffect.url)
            }

            is MeetingDetailSideEffect.ShowToastMessage -> {
                showToast(context, sideEffect.message)
            }
        }
    }

    when {
        uiState.isLoading -> {
            LoadingScreen(modifier)
        }

        uiState.isSuccess -> {
            MeetingDetailScreen(
                modifier = modifier,
                uiState = uiState,
                isLoading = isLoading,
                onIntent = viewModel::onIntent,
            )
        }

        uiState.isError -> {
            ErrorScreen(
                modifier = modifier,
                onClickRefresh = { viewModel.onIntent(MeetingDetailIntent.RefreshClick) },
            )
        }
    }
}

@Composable
fun MeetingDetailScreen(
    modifier: Modifier = Modifier,
    uiState: MeetingDetailState,
    isLoading: Boolean = false,
    onIntent: (MeetingDetailIntent) -> Unit,
) {
    val meeting = uiState.meeting.data ?: return

    TrackScreenViewEvent(screenName = "meet_detail")
    Column(
        modifier = modifier,
    ) {
        MeetingDetailTopAppbar(
            meeting = meeting,
            onIntent = onIntent,
        )

        uiState.notice?.let {
            Box(
                modifier = Modifier.background(MoimTheme.colors.bg.secondary),
            ) {
                MeetingDetailNotice(
                    notice = it,
                    onIntent = onIntent,
                )
            }
        }

        MeetingDetailHeader(
            isSelectedFuturePlan = uiState.isPlanSelected,
            onIntent = onIntent,
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
                onIntent = onIntent,
            )

            MoimFloatingActionButton(
                modifier =
                    Modifier
                        .padding(end = 24.dp, bottom = 20.dp)
                        .size(54.dp)
                        .align(Alignment.BottomEnd),
                onClick = { onIntent(MeetingDetailIntent.PlanWriteClick) },
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_add),
                    contentDescription = "",
                )
            }
        }
    }

    if (uiState.isShowApplyCancelDialog) {
        val dismissIntent = MeetingDetailIntent.PlanApplyCancelDialogShow(false, null)

        MoimAlertDialog(
            title = stringResource(R.string.meeting_detail_plan_cancel),
            positiveButtonColors = moimButtomColors().copy(containerColor = MoimTheme.colors.secondary),
            onDismiss = { onIntent(dismissIntent) },
            onClickNegative = { onIntent(dismissIntent) },
            onClickPositive = {
                if (uiState.cancelPlanItem == null) return@MoimAlertDialog
                onIntent(MeetingDetailIntent.PlanApplyClick(uiState.cancelPlanItem, false))
            },
        )
    }

    LoadingDialog(isLoading)
}

@ThemePreviews
@Composable
private fun MeetingDetailScreenPreview() {
    MoimTheme {
        MeetingDetailScreen(
            uiState =
                MeetingDetailState(
                    userId = "",
                    meeting = Result.Success(Meeting(name = "모닝커피클럽")),
                    notice =
                        MeetingDetailNoticeUiModel(
                            noticeId = "",
                            content = "11/28일 모임 18:00 → 20:00 변경 되었습니다. 날씨이슈로 인해서 부득이하게 변경합니다. 양해 부탁드립니다.",
                            noticeType = NoticeType.CUSTOM,
                        ),
                ),
            isLoading = false,
            onIntent = {},
        )
    }
}
