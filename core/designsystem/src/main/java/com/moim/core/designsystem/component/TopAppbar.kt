package com.moim.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.theme.MoimTheme

@Composable
fun MoimTopAppbar(
    modifier: Modifier = Modifier,
    thickness: Dp = 0.dp,
    dividerColor: Color = MoimTheme.colors.stroke,
    title: @Composable () -> Unit = {},
    backgroundColor: Color = MoimTheme.colors.white,
    isNavigationIconVisible: Boolean = true,
    onClickNavigate: () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
            contentDescription = "back",
        )
    },
    actions: @Composable RowScope.() -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Column {
        CenterAlignedTopAppBar(
            modifier = modifier
                .fillMaxWidth()
                .background(backgroundColor),
            title = title,
            navigationIcon = {
                if (isNavigationIconVisible) {
                    IconButton(
                        modifier = Modifier.padding(vertical = 12.dp),
                        onClick = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            onClickNavigate()
                        }
                    ) {
                        navigationIcon()
                    }
                }
            },
            actions = actions,
            windowInsets = WindowInsets(top = 0.dp),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                navigationIconContentColor = MoimTheme.colors.secondary,
                titleContentColor = MoimTheme.colors.gray.gray01,
                actionIconContentColor = MoimTheme.colors.icon
            )
        )

        if (thickness != 0.dp) {
            HorizontalDivider(thickness = thickness, color = dividerColor)
        }
    }
}

@Composable
fun MoimTopAppbar(
    modifier: Modifier = Modifier,
    thickness: Dp = 0.dp,
    dividerColor: Color = MoimTheme.colors.stroke,
    title: String = "",
    titleStyle: TextStyle = MoimTheme.typography.title02.bold,
    titleColor: Color = MoimTheme.colors.gray.gray01,
    backgroundColor: Color = MoimTheme.colors.white,
    isNavigationIconVisible: Boolean = true,
    onClickNavigate: () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_back),
            contentDescription = "back",
        )
    },
    actions: @Composable RowScope.() -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    Column {
        CenterAlignedTopAppBar(
            modifier = modifier
                .fillMaxWidth()
                .background(backgroundColor),
            title = {
                Text(
                    text = title,
                    style = titleStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                if (isNavigationIconVisible) {
                    IconButton(
                        modifier = Modifier.padding(vertical = 12.dp),
                        onClick = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            onClickNavigate()
                        }
                    ) {
                        navigationIcon()
                    }
                }
            },
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    actions()
                    Spacer(Modifier.width(6.dp))
                }
            },
            windowInsets = WindowInsets(top = 0.dp),
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent,
                navigationIconContentColor = MoimTheme.colors.secondary,
                titleContentColor = titleColor,
                actionIconContentColor = MoimTheme.colors.icon
            )
        )

        if (thickness != 0.dp) {
            HorizontalDivider(thickness = thickness, color = dividerColor)
        }
    }
}

@Preview
@Composable
private fun MoimTopAppbarPreview() {
    MoimTheme {
        MoimTopAppbar(
            modifier = Modifier.fillMaxWidth(),
            title = "타이틀",
            onClickNavigate = {},
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_pen),
                        contentDescription = ""
                    )
                }
            }
        )
    }
}