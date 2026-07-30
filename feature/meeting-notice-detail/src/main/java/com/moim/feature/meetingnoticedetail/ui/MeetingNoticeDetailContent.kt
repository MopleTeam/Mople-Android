package com.moim.feature.meetingnoticedetail.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.NoticeType
import com.moim.core.common.util.parseDateString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.meetingnoticedetail.model.NoticeUiModel
import java.time.ZonedDateTime

@Composable
fun MeetingNoticeDetailContent(
    notice: NoticeUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(20.dp),
    ) {
        MeetingNoticeDetailHeader(notice = notice)

        Spacer(Modifier.height(16.dp))

        Text(
            text = notice.content,
            style = MoimTheme.typography.body01.regular,
            color = MoimTheme.colors.text.text01,
        )
    }
}

@Composable
private fun MeetingNoticeDetailHeader(
    notice: NoticeUiModel,
    modifier: Modifier = Modifier,
) {
    val writerNickname =
        if (notice.type == NoticeType.CUSTOM) {
            notice.writerNickname
        } else {
            stringResource(R.string.meeting_notice_detail_writer_system)
        }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NetworkImage(
            modifier =
                Modifier
                    .clip(CircleShape)
                    .border(BorderStroke(1.dp, MoimTheme.colors.stroke), CircleShape)
                    .size(32.dp),
            imageUrl = notice.writerImageUrl,
            errorImage = painterResource(R.drawable.ic_empty_user_logo),
        )

        Spacer(Modifier.width(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            MoimText(
                text = writerNickname,
                style = MoimTheme.typography.body01.semiBold,
                color = MoimTheme.colors.text.text01,
            )
            MoimText(
                text = notice.createdAt.parseDateString(stringResource(R.string.regex_date_year_month_day_short)),
                style = MoimTheme.typography.body02.regular,
                color = MoimTheme.colors.text.text03,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun MeetingNoticeDetailContentPreview() {
    MoimTheme {
        Column {
            MeetingNoticeDetailContent(
                notice =
                    NoticeUiModel(
                        noticeId = "",
                        writerNickname = "소보루붕어빵",
                        writerImageUrl = "",
                        type = NoticeType.CUSTOM,
                        content = "11/28일 모임 18:00 → 20:00 변경\n날씨 이슈로 인해서 부득이하게 변경했습니다!",
                        createdAt = ZonedDateTime.now(),
                    ),
            )
        }
    }
}
