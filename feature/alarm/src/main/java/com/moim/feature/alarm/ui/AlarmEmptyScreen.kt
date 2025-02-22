package com.moim.feature.alarm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.theme.MoimTheme

@Composable
fun AlarmEmptyScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        Icon(
            modifier = Modifier.size(80.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_alarm),
            contentDescription = "",
            tint = MoimTheme.colors.icon
        )
        MoimText(
            text = stringResource(R.string.alarm_empty),
            style = MoimTheme.typography.body01.medium,
            color = MoimTheme.colors.gray.gray06
        )

        Spacer(Modifier.weight(2f))
    }
}
