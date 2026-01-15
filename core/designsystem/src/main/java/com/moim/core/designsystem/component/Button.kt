package com.moim.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.color_FEE500
import com.moim.core.designsystem.theme.moimButtomColors

@Composable
fun MoimPrimaryButton(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 12.dp,
    verticalPadding: Dp = 18.dp,
    buttonColors: ButtonColors = moimButtomColors(),
    shapes: RoundedCornerShape = RoundedCornerShape(8.dp),
    enable: Boolean = true,
    text: String = "",
    style: TextStyle = MoimTheme.typography.body01.semiBold,
    onClick: () -> Unit = {},
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = buttonColors,
        enabled = enable,
        shape = shapes,
        contentPadding =
            PaddingValues(
                vertical = verticalPadding,
                horizontal = horizontalPadding,
            ),
        content = {
            Text(
                text = text,
                style = style,
            )
        },
    )
}

@Composable
fun MoimPrimaryButton(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 12.dp,
    verticalPadding: Dp = 18.dp,
    buttonColors: ButtonColors = moimButtomColors(),
    shapes: RoundedCornerShape = RoundedCornerShape(8.dp),
    enable: Boolean = true,
    onClick: () -> Unit = {},
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = buttonColors,
        enabled = enable,
        shape = shapes,
        contentPadding =
            PaddingValues(
                vertical = verticalPadding,
                horizontal = horizontalPadding,
            ),
        content = content,
    )
}

@Composable
fun MoimIconButton(
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    shape: Shape = RoundedCornerShape(100),
    @DrawableRes iconRes: Int,
    iconColor: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
    onClick: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .defaultMinSize(minHeight = 40.dp, minWidth = 40.dp)
                .clip(shape)
                .background(backgroundColor)
                .onSingleClick(
                    enabled = enable,
                    onClick = onClick,
                    role = Role.Button,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = "",
            tint = iconColor,
        )
    }
}

@Composable
fun MoimFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(100),
    containerColor: Color = MoimTheme.colors.primary.primary,
    contentColor: Color = MoimTheme.colors.white,
    borderStrokeColor: Color = MoimTheme.colors.primary.primary,
    minWidth: Dp = 44.dp,
    minHeight: Dp = 44.dp,
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 6.dp,
    borderStrokeSize: Dp = 0.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        interactionSource = interactionSource,
        border = BorderStroke(borderStrokeSize, borderStrokeColor),
    ) {
        Box(
            modifier =
                Modifier
                    .defaultMinSize(
                        minWidth = minWidth,
                        minHeight = minHeight,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun MoimPrimaryButtonPreview() {
    MoimTheme {
        Column(
            modifier =
                Modifier
                    .background(MoimTheme.colors.white)
                    .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Primary Button",
                style = MoimTheme.typography.title03.semiBold,
            )
            MoimPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = "로그인",
                buttonColors = moimButtomColors(),
            )

            MoimPrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                buttonColors =
                    moimButtomColors().copy(
                        containerColor = color_FEE500,
                        contentColor = MoimTheme.colors.gray.gray01,
                    ),
                onClick = {},
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_kakao),
                        contentDescription = "",
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.sign_in_kakao),
                        style = MoimTheme.typography.body01.semiBold,
                    )
                }
            }

            Text(
                text = "Etc Button",
                style = MoimTheme.typography.title03.semiBold,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                MoimIconButton(
                    iconRes = R.drawable.ic_alarm,
                )

                MoimFloatingActionButton(
                    onClick = {},
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_add),
                        contentDescription = "",
                    )
                }
            }
        }
    }
}
