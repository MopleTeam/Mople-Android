package com.moim.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.theme.MoimTheme

@Composable
fun MoimCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    border: BorderStroke? = null,
    elevation: CardElevation = CardDefaults.cardElevation(),
    color: Color = MoimTheme.colors.white,
    onClick: () -> Unit = {},
    enable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .clip(shape)
                .onSingleClick(enabled = enable, onClick = onClick),
    ) {
        Card(
            modifier = modifier,
            colors =
                CardDefaults.cardColors(
                    containerColor = color,
                    contentColor = color,
                ),
            elevation = elevation,
            shape = shape,
            border = border,
            content = content,
        )
    }
}
