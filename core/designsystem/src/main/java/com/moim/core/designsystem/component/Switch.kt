package com.moim.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SwitchColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.internal.MoimSwitchImpl
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimSwitchColors

@Composable
fun MoimSwitch(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    colors: SwitchColors = moimSwitchColors(),
    onCheckedChange: (Boolean) -> Unit,
) {
    MoimSwitchImpl(
        modifier = modifier,
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        colors = colors,
    )
}

@ThemePreviews
@Composable
private fun MoimSwitchPreview() {
    MoimTheme {
        Column {
            MoimSwitch(
                isChecked = true,
                onCheckedChange = {},
            )
            MoimSwitch(
                isChecked = false,
                onCheckedChange = {},
            )
        }
    }
}
