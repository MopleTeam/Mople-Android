package com.moim.feature.meetingdetail

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moim.core.common.view.ObserveAsEvents
import com.moim.core.common.view.showToast
import com.moim.core.designsystem.R
import com.moim.core.designsystem.common.ErrorScreen
import com.moim.core.designsystem.common.LoadingDialog
import com.moim.core.designsystem.common.LoadingScreen
import com.moim.core.designsystem.component.MoimFloatingActionButton
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.containerScreen
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.Meeting
import com.moim.core.model.Plan
import com.moim.core.model.item.PlanItem
import com.moim.core.model.item.asPlanItem
import com.moim.feature.meetingdetail.ui.MeetingDetailHeader
import com.moim.feature.meetingdetail.ui.MeetingDetailPlanContent
import com.moim.feature.meetingdetail.ui.MeetingDetailPlanEmpty

internal typealias OnMeetingDetailUiAction = (MeetingDetailUiAction) -> Unit

@Composable
fun MeetingDetailRoute(
    padding: PaddingValues,
    viewModel: MeetingDetailViewModel = hiltViewModel(),
    navigateToBack: () -> Unit,
    navigateToPlanWrite: (PlanItem) -> Unit,
    navigateToPlanDetail: (String, Boolean) -> Unit,
    navigateToMeetingSetting: (Meeting) -> Unit
) {
    val context = LocalContext.current
    val isLoading by viewModel.loading.collectAsStateWithLifecycle()
    val meetingDetailUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val modifier = Modifier.containerScreen(padding, MoimTheme.colors.white)

    ObserveAsEvents(viewModel.uiEvent) { event ->
        when (event) {
            is MeetingDetailUiEvent.NavigateToBack -> navigateToBack()
            is MeetingDetailUiEvent.NavigateToMeetingSetting -> navigateToMeetingSetting(event.meeting)
            is MeetingDetailUiEvent.NavigateToPlanDetail -> navigateToPlanDetail(event.postId, event.isPlan)
            is MeetingDetailUiEvent.NavigateToPlanWrite -> navigateToPlanWrite(event.plan.asPlanItem())
            is MeetingDetailUiEvent.ShowToastMessage -> showToast(context, event.message)
        }
    }

    when (val uiState = meetingDetailUiState) {
        is MeetingDetailUiState.Loading -> LoadingScreen(modifier)

        is MeetingDetailUiState.Success -> MeetingDetailScreen(
            modifier = modifier,
            uiState = uiState,
            isLoading = isLoading,
            onUiAction = viewModel::onUiAction
        )

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
    onUiAction: OnMeetingDetailUiAction = {}
) {
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
            if (uiState.isPlanSelected) {
                if (uiState.plans.isEmpty()) {
                    MeetingDetailPlanEmpty()
                } else {
                    MeetingDetailPlanContent(
                        userId = uiState.userId,
                        plans = uiState.plans,
                        reviews = uiState.reviews,
                        isPlanSelected = true,
                        onUiAction = onUiAction
                    )
                }
            } else {
                if (uiState.reviews.isEmpty()) {
                    MeetingDetailPlanEmpty()
                } else {
                    MeetingDetailPlanContent(
                        userId = uiState.userId,
                        plans = uiState.plans,
                        reviews = uiState.reviews,
                        isPlanSelected = false,
                        onUiAction = onUiAction
                    )
                }
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

    LoadingDialog(isLoading)
}

@Preview
@Composable
private fun MeetingDetailScreenPreview() {
    MoimTheme {
        MeetingDetailScreen(
            modifier = Modifier.containerScreen(backgroundColor = MoimTheme.colors.white),
            uiState = MeetingDetailUiState.Success(
                userId = "",
                meeting = Meeting(
                    name = "우리 중학교 동창",
                    memberCount = 3,
                ),
                plans = emptyList()
            ),
        )
    }
}