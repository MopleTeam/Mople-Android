package com.moim.feature.plandetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimDialog
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.plandetail.OnPlanDetailUiAction
import com.moim.feature.plandetail.PlanDetailUiAction

@Composable
fun PlanDetailImageCropDialog(
    modifier: Modifier = Modifier,
    images: List<String>,
    selectedIndex : Int,
    onUiAction: OnPlanDetailUiAction
) {
    val onDismissAction = PlanDetailUiAction.OnShowReviewImageCropDialog(false, 0)
    val pageSize = images.size
    val pagerState = rememberPagerState(
        pageCount = { pageSize },
        initialPage = selectedIndex
    )

    MoimDialog(
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = false,
        onDismiss = { onUiAction(onDismissAction) },
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MoimTheme.colors.black)
        ) {
            ImageCropDialogTopAppbar(
                currentIndex = pagerState.currentPage + 1,
                totalIndex = pageSize,
                onClickDismiss = {
                    onUiAction(onDismissAction)
                }
            )
            HorizontalPager(
                modifier = Modifier.fillMaxWidth(),
                state = pagerState,
            ) { index ->
                NetworkImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 20.dp),
                    imageUrl = images[index]
                )
            }
        }
    }
}

@Composable
private fun ImageCropDialogTopAppbar(
    currentIndex: Int,
    totalIndex: Int,
    onClickDismiss: () -> Unit
) {
    MoimTopAppbar(
        backgroundColor = MoimTheme.colors.black,
        navigationIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_back),
                contentDescription = "",
                tint = MoimTheme.colors.white
            )
        },
        onClickNavigate = onClickDismiss,
        title = stringResource(R.string.plan_detail_image),
        titleColor = MoimTheme.colors.white,
        actions = {
            MoimText(
                text = "$currentIndex/$totalIndex",
                style = MoimTheme.typography.title03.semiBold,
                color = MoimTheme.colors.gray.gray04,
            )
        }
    )
}