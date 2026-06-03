package com.moim.feature.meetingnotice.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.Notice
import com.moim.core.common.model.NoticeType
import com.moim.core.common.util.parseDateString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.meetingnotice.MeetingNoticeUiAction
import java.time.ZonedDateTime

@Composable
fun MeetingNoticeItem(
    notice: Notice,
    onUiAction: (MeetingNoticeUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .onSingleClick { onUiAction(MeetingNoticeUiAction.OnClickNotice(notice)) }
                .padding(20.dp),
    ) {
        NoticeIcon(type = notice.type)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            MoimText(
                modifier = Modifier.fillMaxWidth(),
                text = notice.content,
                style = MoimTheme.typography.body01.medium,
                color = MoimTheme.colors.text.text02,
            )
            Row {
                MoimText(
                    text = notice.createdAt.parseDateString(stringResource(R.string.regex_date_year_month_day_short)),
                    style = MoimTheme.typography.body02.regular,
                    color = MoimTheme.colors.text.text03,
                )
            }
        }
    }
}

@Composable
private fun NoticeIcon(
    type: NoticeType,
    modifier: Modifier = Modifier,
) {
    val (iconRes, iconColor) =
        when (type) {
            NoticeType.NONE,
            NoticeType.CUSTOM,
            -> R.drawable.ic_notice to MoimTheme.colors.global.primary

            NoticeType.SYSTEM -> R.drawable.ic_notice_system to MoimTheme.colors.gray.gray06
        }

    Icon(
        modifier = modifier.size(24.dp),
        imageVector = ImageVector.vectorResource(iconRes),
        contentDescription = null,
        tint = iconColor,
    )
}

@ThemePreviews
@Composable
private fun MeetingNoticeItemPreview() {
    MoimTheme {
        val notice =
            Notice(
                noticeId = "",
                version = 1,
                meetId = "",
                type = NoticeType.CUSTOM,
                content = "11/28일 모임 18:00 → 20:00 변경, 날씨이슈로 인해서 부득이하게 변경했습니다!",
                createdAt = ZonedDateTime.now(),
                pinned = false,
            )

        Column {
            MeetingNoticeItem(
                notice = notice,
                onUiAction = {},
            )
            MeetingNoticeItem(
                notice = notice.copy(type = NoticeType.SYSTEM),
                onUiAction = {},
            )
        }
    }
}
