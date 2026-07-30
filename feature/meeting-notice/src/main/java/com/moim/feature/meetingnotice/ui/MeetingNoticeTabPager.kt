package com.moim.feature.meetingnotice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.theme.MoimTheme
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun MeetingNoticeTabPager(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val titles =
        listOf(
            stringResource(R.string.meeting_notice_tab_total),
            stringResource(R.string.meeting_notice_tab_custom),
            stringResource(R.string.meeting_notice_tab_system),
        )

    SecondaryScrollableTabRow(
        modifier = modifier,
        selectedTabIndex = pagerState.currentPage,
        containerColor = Color.Transparent,
        contentColor = Color.Transparent,
        edgePadding = 0.dp,
        minTabWidth = 0.dp,
        indicator = {
            val indicatorLayoutModifier =
                Modifier.tabIndicatorLayout { measurable, constraints, tabPositions ->
                    if (tabPositions.isEmpty()) {
                        val placeable = measurable.measure(constraints)
                        return@tabIndicatorLayout layout(constraints.maxWidth, placeable.height) {}
                    }
                    val currentPage = pagerState.currentPage.coerceIn(0, tabPositions.lastIndex)
                    val fraction = pagerState.currentPageOffsetFraction
                    val currentTab = tabPositions[currentPage]
                    val targetTab =
                        when {
                            fraction > 0f -> tabPositions.getOrNull(currentPage + 1)
                            fraction < 0f -> tabPositions.getOrNull(currentPage - 1)
                            else -> null
                        } ?: currentTab
                    val absFraction = abs(fraction)
                    val indicatorLeft = lerp(currentTab.left, targetTab.left, absFraction).roundToPx()
                    val indicatorWidth = lerp(currentTab.width, targetTab.width, absFraction).roundToPx()

                    val placeable =
                        measurable.measure(
                            constraints.copy(minWidth = indicatorWidth, maxWidth = indicatorWidth),
                        )
                    layout(currentTab.width.roundToPx(), placeable.height) {
                        placeable.place(indicatorLeft, 0)
                    }
                }
            Box(
                modifier =
                    indicatorLayoutModifier
                        .height(2.dp)
                        .background(MoimTheme.colors.secondary),
            )
        },
        divider = {
            HorizontalDivider(
                thickness = 2.dp,
                color = MoimTheme.colors.bg.secondary,
            )
        },
    ) {
        titles.forEachIndexed { index, title ->
            val isSelected = pagerState.currentPage == index
            Tab(
                selected = isSelected,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
            ) {
                MoimText(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    text = title,
                    style =
                        if (isSelected) {
                            MoimTheme.typography.body01.semiBold
                        } else {
                            MoimTheme.typography.body01.regular
                        },
                    color = MoimTheme.colors.text.text02,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun MeetingNoticeTabPagerPreview() {
    MoimTheme {
        MeetingNoticeTabPager(
            pagerState = rememberPagerState { 3 },
        )
    }
}
