package com.moim.feature.main.screen.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_3E3F40
import com.moim.core.designsystem.theme.color_999999
import com.moim.core.designsystem.theme.color_FFFFFF
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
        enter = fadeIn() + slideIn { IntOffset(0, it.height) },
        exit = fadeOut() + slideOut { IntOffset(0, it.height) },
    ) {
        Box(
            modifier = modifier.background(color = Color.Transparent)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(color_FFFFFF, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
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
            .selectable(
                selected = isSelected,
                indication = null,
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
                tint = if (isSelected) color_3E3F40 else color_999999,
            )
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = tab.label,
                style = MoimTheme.typography.body02.regular.copy(fontSize = 10.sp),
                color = if (isSelected) color_3E3F40 else color_999999,
            )
        }
    }
}