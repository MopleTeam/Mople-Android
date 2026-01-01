package com.moim.feature.main.screen.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
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
    tabs: List<MainTab>,
    currentTab: MainTab?,
    onTabSelected: (MainTab) -> Unit,
) {
    NavigationBar(
        containerColor = MoimTheme.colors.white
    ) {
        tabs.forEach { tab ->
            MainBottomBarItem(
                tab = tab,
                isSelected = currentTab == tab,
                onClick = { onTabSelected(tab) },
            )
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