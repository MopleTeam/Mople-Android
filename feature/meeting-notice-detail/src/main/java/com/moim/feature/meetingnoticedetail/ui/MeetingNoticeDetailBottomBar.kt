package com.moim.feature.meetingnoticedetail.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimTextField
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.meetingnoticedetail.OnMeetingNoticeDetailIntent
import com.moim.feature.meetingnoticedetail.model.MeetingNoticeDetailIntent

@Composable
fun MeetingNoticeDetailBottomBar(
    modifier: Modifier = Modifier,
    commentState: TextFieldState = TextFieldState(),
    onIntent: OnMeetingNoticeDetailIntent = {},
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val hasText = commentState.text.isNotEmpty()

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 20.dp),
    ) {
        MoimTextField(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(end = 52.dp)
                    .align(Alignment.CenterStart),
            hintText = stringResource(R.string.meeting_notice_detail_comment_hint),
            singleLine = false,
            textFieldState = commentState,
            textStyle = MoimTheme.typography.body01.regular,
        )

        MoimIconButton(
            modifier =
                Modifier
                    .size(40.dp)
                    .align(Alignment.CenterEnd),
            iconRes = R.drawable.ic_arrow_up,
            backgroundColor = if (hasText) MoimTheme.colors.global.primary else MoimTheme.colors.primary.disable,
            enable = hasText,
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                onIntent(MeetingNoticeDetailIntent.CommentUploadClick)
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MeetingNoticeDetailBottomBarPreview() {
    MoimTheme {
        MeetingNoticeDetailBottomBar(
            commentState = TextFieldState("공지 잘 봤습니다!"),
        )
    }
}
