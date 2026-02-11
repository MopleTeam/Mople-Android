package com.moim.core.designsystem

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "1. Light",
    uiMode = UI_MODE_NIGHT_NO,
    backgroundColor = 0XFFFFFF,
    showBackground = true,
)
@Preview(
    name = "2. Dark",
    uiMode = UI_MODE_NIGHT_YES,
    backgroundColor = 0xFF171717,
    showBackground = true,
)
annotation class ThemePreviews

@Preview(name = "foldable", device = "spec:width=680dp,height=841dp,dpi=480")
annotation class FoldablePreviews
