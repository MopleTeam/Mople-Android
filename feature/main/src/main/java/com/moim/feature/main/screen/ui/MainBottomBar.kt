package com.moim.feature.main.screen.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.main.navigation.MainTab

@Composable
fun MainBottomBar(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    tabs: List<MainTab>,
    currentTab: MainTab?,
    onTabSelected: (MainTab) -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = EnterTransition.None,
        exit = ExitTransition.None,
    ) {
        Box(
            modifier = modifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(MoimTheme.colors.white)
                    .border(width = 1.dp, color = MoimTheme.colors.stroke),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    MainBottomBarItem(
                        tab = tab,
                        isSelected = tab == currentTab,
                        onClick = { if (tab != currentTab) onTabSelected(tab) }
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.MainBottomBarItem(
    tab: MainTab,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(50))
            .selectable(
                selected = isSelected,
                indication = ripple(bounded = true, color = MoimTheme.colors.gray.gray01),
                role = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(tab.iconResId),
                contentDescription = tab.contentDescription,
                tint = if (isSelected) MoimTheme.colors.secondary else MoimTheme.colors.icon,
            )
            Spacer(modifier = Modifier.height(5.dp))

            MoimText(
                text = tab.label,
                style = MoimTheme.typography.body02.regular.copy(fontSize = 10.sp),
                color = if (isSelected) MoimTheme.colors.secondary else MoimTheme.colors.gray.gray05,
            )
        }
    }
}