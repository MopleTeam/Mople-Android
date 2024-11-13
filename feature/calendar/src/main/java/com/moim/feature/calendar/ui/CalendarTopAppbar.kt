package com.moim.feature.calendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.feature.calendar.CalendarUiAction
import com.moim.feature.calendar.OnCalendarUiAction
import java.time.ZonedDateTime

@Composable
fun CalendarTopAppbar(
    modifier: Modifier = Modifier,
    currentDate: ZonedDateTime,
    onUiAction: OnCalendarUiAction = {}
) {
    MoimTopAppbar(
        modifier = modifier,
        title = stringResource(R.string.calendar_title),
        isNavigationIconVisible = false,
        actions = {
            MoimIconButton(
                iconRes = R.drawable.ic_calendar,
                onClick = { onUiAction(CalendarUiAction.OnClickExpandable(currentDate)) },
            )
        }
    )
}