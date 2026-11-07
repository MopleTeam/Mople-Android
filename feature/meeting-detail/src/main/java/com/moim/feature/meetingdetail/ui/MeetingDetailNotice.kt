package com.moim.feature.meetingdetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.NoticeType
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.meetingdetail.model.MeetingDetailNoticeUiModel
import com.moim.feature.meetingdetail.model.MeetingDetailIntent

@Composable
fun MeetingDetailNotice(
    notice: MeetingDetailNoticeUiModel,
    onIntent: (MeetingDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 16.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MoimTheme.colors.bg.primary)
                .onSingleClick {
                    onIntent(MeetingDetailIntent.MeetingNoticeDetailClick(notice.noticeId))
                }.padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NoticeIcon(notice.noticeType)
            MoimText(
                text = stringResource(R.string.meeting_detail_notice),
                style = MoimTheme.typography.body02.semiBold,
                color = MoimTheme.colors.text.text03,
            )
        }
        Spacer(Modifier.height(4.dp))
        MoimText(
            text = notice.content,
            style = MoimTheme.typography.body01.semiBold,
            color = MoimTheme.colors.text.text02,
        )
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
private fun MeetingDetailNoticePreview() {
    MoimTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MoimTheme.colors.bg.secondary),
        ) {
            MeetingDetailNotice(
                notice =
                    MeetingDetailNoticeUiModel(
                        noticeId = "",
                        noticeType = NoticeType.CUSTOM,
                        content = "11/28일 모임 18:00 → 20:00 변경 되었습니다. 날씨이슈로 인해서 부득이하게 변경합니다. 양해 부탁드립니다.",
                    ),
                onIntent = {},
            )
        }
    }
}
