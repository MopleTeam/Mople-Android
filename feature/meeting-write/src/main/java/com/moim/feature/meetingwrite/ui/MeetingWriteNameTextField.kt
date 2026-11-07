package com.moim.feature.meetingwrite.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTextField
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimTextFieldColors
import com.moim.feature.meetingwrite.OnMeetingWriteIntent
import com.moim.feature.meetingwrite.model.MeetingWriteIntent

@Composable
fun MeetingWriteNameTextField(
    meetingName: String,
    onIntent: OnMeetingWriteIntent = {},
) {
    MoimText(
        text = stringResource(R.string.meeting_write_name),
        style = MoimTheme.typography.title03.semiBold,
        color = MoimTheme.colors.text.text01,
    )
    Spacer(Modifier.height(8.dp))
    MoimTextField(
        hintText = stringResource(R.string.meeting_write_hint),
        textFieldColors = moimTextFieldColors(),
        text = meetingName,
        textMaxLength = 30,
        onTextChanged = { onIntent(MeetingWriteIntent.MeetingNameChange(it)) },
    )
}
