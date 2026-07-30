package com.moim.feature.meetingnotice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.NoticeType
import com.moim.core.common.util.parseDateString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.meetingnotice.MeetingNoticeUiAction
import com.moim.feature.meetingnotice.model.NoticeUiModel
import java.time.ZonedDateTime
import kotlin.math.roundToInt

private val PIN_ACTION_WIDTH = 60.dp

private enum class NoticePinDragValue {
    Closed,
    Open,
}

@Composable
fun MeetingNoticeItem(
    notice: NoticeUiModel,
    isHostUser: Boolean,
    openedNoticeId: String?,
    onOpenedChange: (String?) -> Unit,
    onUiAction: (MeetingNoticeUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    // 모임장만, 그리고 System이 아닌 Custom 공지만 고정할 수 있으므로 이 경우에만 드래그를 허용한다.
    val canPin = isHostUser && notice.type == NoticeType.CUSTOM
    val density = LocalDensity.current
    val dragState =
        remember(density) {
            AnchoredDraggableState(initialValue = NoticePinDragValue.Closed).apply {
                updateAnchors(
                    DraggableAnchors {
                        NoticePinDragValue.Closed at 0f
                        NoticePinDragValue.Open at with(density) { PIN_ACTION_WIDTH.toPx() }
                    },
                )
            }
        }

    // 이 항목이 열리면 화면에 현재 열린 항목으로 기록하고, 스스로 닫히면 해제한다.
    LaunchedEffect(dragState.settledValue) {
        when {
            dragState.settledValue == NoticePinDragValue.Open -> onOpenedChange(notice.noticeId)
            openedNoticeId == notice.noticeId -> onOpenedChange(null)
        }
    }

    // 다른 항목이 열리면 이 항목은 닫는다.
    LaunchedEffect(openedNoticeId) {
        if (openedNoticeId != notice.noticeId && dragState.currentValue != NoticePinDragValue.Closed) {
            dragState.animateTo(NoticePinDragValue.Closed)
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        if (canPin) {
            PinAction(
                pinned = notice.pinned,
                onClick = {
                    onUiAction(MeetingNoticeUiAction.OnClickPin(notice))
                    // 고정/해제 실행 후 열린 스와이프를 닫는다.
                    onOpenedChange(null)
                },
            )
        }

        Row(
            modifier =
                Modifier
                    .offset { IntOffset(x = dragState.requireOffset().roundToInt(), y = 0) }
                    .anchoredDraggable(
                        state = dragState,
                        orientation = Orientation.Horizontal,
                        enabled = canPin,
                    ).fillMaxWidth()
                    .background(MoimTheme.colors.bg.primary)
                    .onSingleClick { onUiAction(MeetingNoticeUiAction.OnClickNotice(notice)) }
                    .padding(20.dp),
        ) {
            if (notice.pinned) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.ic_pin),
                    contentDescription = null,
                    tint = MoimTheme.colors.icon,
                )
            }

            NoticeIcon(type = notice.type)

            Spacer(Modifier.width(8.dp))

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
}

@Composable
private fun BoxScope.PinAction(
    pinned: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 고정 해제(이미 고정된 공지)는 빨간색, 고정은 파란색 박스로 노출한다.
    val backgroundColor = if (pinned) MoimTheme.colors.global.red else MoimTheme.colors.global.primary

    Box(
        modifier =
            modifier
                .matchParentSize(),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .width(PIN_ACTION_WIDTH)
                    .background(backgroundColor)
                    .onSingleClick(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_pin),
                contentDescription = null,
                tint = MoimTheme.colors.global.white,
            )
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
            NoticeUiModel(
                noticeId = "",
                meetId = "",
                type = NoticeType.CUSTOM,
                content = "11/28일 모임 18:00 → 20:00 변경, 날씨이슈로 인해서 부득이하게 변경했습니다!",
                createdAt = ZonedDateTime.now(),
                pinned = true,
            )

        Column {
            MeetingNoticeItem(
                notice = notice,
                isHostUser = true,
                openedNoticeId = null,
                onOpenedChange = {},
                onUiAction = {},
            )
            MeetingNoticeItem(
                notice = notice.copy(type = NoticeType.SYSTEM, pinned = false),
                isHostUser = true,
                openedNoticeId = null,
                onOpenedChange = {},
                onUiAction = {},
            )
        }
    }
}
