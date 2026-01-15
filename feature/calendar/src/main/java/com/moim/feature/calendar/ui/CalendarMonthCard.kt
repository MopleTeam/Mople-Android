package com.moim.feature.calendar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.common.util.parseDateString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimCard
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.theme.MoimTheme
import java.time.ZonedDateTime

@Composable
fun CalendarMonthCard(
    modifier: Modifier = Modifier,
    selectDate: ZonedDateTime,
) {
    MoimCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        enable = false,
        color = MoimTheme.colors.bg.primary,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            MoimText(
                text = selectDate.parseDateString(stringResource(R.string.regex_date_year_month)),
                singleLine = false,
                style = MoimTheme.typography.title03.semiBold,
                color = MoimTheme.colors.gray.gray01,
            )
        }
    }
}

@Preview
@Composable
private fun CalendarMonthCardPreview() {
    MoimTheme {
        CalendarMonthCard(
            selectDate = ZonedDateTime.now(),
        )
    }
}
