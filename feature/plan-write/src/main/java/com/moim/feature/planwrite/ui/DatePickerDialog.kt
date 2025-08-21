package com.moim.feature.planwrite.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.common.util.default
import com.moim.core.common.util.parseZonedDateTime
import com.moim.core.designsystem.R
import com.moim.core.designsystem.theme.MoimTheme
import java.time.ZonedDateTime

@Composable
fun MoimDatePickerDialog(
    modifier: Modifier = Modifier,
    date: Long,
    onDateSelected: (date: ZonedDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    val currentTime = ZonedDateTime.now().default()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val selectableDate = utcTimeMillis.parseZonedDateTime().default()
                return selectableDate.isEqual(currentTime) || selectableDate.isAfter(currentTime)
            }
        }
    )

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val longTime = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    onDateSelected(longTime.parseZonedDateTime().default())
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.common_confirm),
                    style = MoimTheme.typography.title03.bold,
                    color = MoimTheme.colors.gray.gray01
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = R.string.common_cancel),
                    style = MoimTheme.typography.title03.bold,
                    color = MoimTheme.colors.gray.gray01
                )
            }
        }
    ) {
        MoimTheme {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors()
            )
        }
    }
}

@Composable
fun MoimTimePickerDialog(
    modifier: Modifier = Modifier,
    date: ZonedDateTime,
    onDateSelected: (date: ZonedDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = date.hour,
        initialMinute = date.minute,
        is24Hour = false,
    )

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(date.withHour(timePickerState.hour).withMinute(timePickerState.minute))
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(id = R.string.common_confirm),
                    style = MoimTheme.typography.title03.bold,
                    color = MoimTheme.colors.gray.gray01
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = R.string.common_cancel),
                    style = MoimTheme.typography.title03.bold,
                    color = MoimTheme.colors.gray.gray01
                )
            }
        }
    ) {
        TimePicker(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            state = timePickerState,
            colors = TimePickerDefaults.colors()
        )
    }
}