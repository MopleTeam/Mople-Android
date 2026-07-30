package com.moim.feature.meetingnoticedetail.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.NoticeComment
import com.moim.core.common.model.OpenGraph
import com.moim.core.common.model.Writer
import com.moim.core.common.model.item.NoticeCommentTextUiModel
import com.moim.core.common.model.item.NoticeCommentUiModel
import com.moim.core.common.util.parseDateString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.ThemePreviews
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.ui.util.decimalFormatString
import com.moim.feature.meetingnoticedetail.MeetingNoticeDetailUiAction
import com.moim.feature.meetingnoticedetail.OnMeetingNoticeDetailUiAction
import java.time.ZonedDateTime

@Composable
fun MeetingNoticeDetailCommentHeader(
    modifier: Modifier = Modifier,
    commentCount: Int,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 28.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MoimText(
            text = stringResource(R.string.plan_detail_comment),
            style = MoimTheme.typography.title03.semiBold,
            color = MoimTheme.colors.text.text01,
        )

        Spacer(Modifier.weight(1f))

        MoimText(
            text = stringResource(R.string.unit_count, commentCount.decimalFormatString()),
            style = MoimTheme.typography.title03.semiBold,
            color = MoimTheme.colors.text.text03,
        )
    }
}

@Composable
fun MeetingNoticeDetailCommentItem(
    modifier: Modifier = Modifier,
    userId: String,
    comment: NoticeCommentUiModel,
    onUiAction: OnMeetingNoticeDetailUiAction,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(20.dp),
    ) {
        NetworkImage(
            modifier =
                Modifier
                    .padding(top = 4.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(BorderStroke(1.dp, MoimTheme.colors.stroke), CircleShape),
            imageUrl = comment.comment.writer.imageUrl,
            errorImage = painterResource(R.drawable.ic_empty_user_logo),
        )

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
        ) {
            CommentHeader(
                userId = userId,
                comment = comment.comment,
                onUiAction = onUiAction,
            )

            if (comment.texts.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                CommentText(
                    texts = comment.texts,
                    onUiAction = onUiAction,
                )
            }

            if (comment.openGraph != null) {
                Spacer(Modifier.height(8.dp))
                CommentOpenGraph(
                    openGraph = requireNotNull(comment.openGraph),
                    onUiAction = onUiAction,
                )
            }
        }
    }

    HorizontalDivider(
        thickness = 1.dp,
        color = MoimTheme.colors.stroke,
    )
}

@Composable
private fun CommentHeader(
    modifier: Modifier = Modifier,
    userId: String,
    comment: NoticeComment,
    onUiAction: OnMeetingNoticeDetailUiAction,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MoimText(
            text = comment.writer.nickname,
            style = MoimTheme.typography.body01.semiBold,
            color = MoimTheme.colors.text.text01,
        )

        Spacer(Modifier.width(8.dp))

        MoimText(
            modifier = Modifier.weight(1f),
            text = comment.commentAt.parseDateString(stringResource(R.string.regex_date_month_day)),
            style = MoimTheme.typography.body02.regular,
            color = MoimTheme.colors.text.text03,
        )

        MoimIconButton(
            iconRes = R.drawable.ic_more,
            onClick = {
                val uiAction =
                    if (userId == comment.writer.userId) {
                        MeetingNoticeDetailUiAction.OnShowCommentEditDialog(
                            isShow = true,
                            comment = comment,
                        )
                    } else {
                        MeetingNoticeDetailUiAction.OnShowCommentReportDialog(
                            isShow = true,
                            comment = comment,
                        )
                    }

                onUiAction(uiAction)
            },
        )
    }
}

@Composable
private fun CommentText(
    modifier: Modifier = Modifier,
    texts: List<NoticeCommentTextUiModel>,
    onUiAction: OnMeetingNoticeDetailUiAction,
) {
    val text = texts.joinToString("") { it.content }
    val spanStyle =
        SpanStyle(
            color = MoimTheme.colors.text.text02,
            fontFamily = FontFamily(Font(R.font.pretendard_medium, FontWeight.W600)),
            fontWeight = FontWeight.W600,
            textDecoration = TextDecoration.None,
        )
    val annotatedText =
        buildAnnotatedString {
            texts.forEach { uiModel ->
                when (uiModel) {
                    is NoticeCommentTextUiModel.PlainText -> {
                        withStyle(style = spanStyle) {
                            append(uiModel.content)
                        }
                    }

                    is NoticeCommentTextUiModel.HyperLinkText -> {
                        val startIndex = text.indexOf(uiModel.content)

                        withStyle(
                            style =
                                spanStyle.copy(
                                    color = MoimTheme.colors.global.primary,
                                    textDecoration = TextDecoration.Underline,
                                ),
                        ) {
                            append(uiModel.content)
                        }
                        addLink(
                            clickable =
                                LinkAnnotation.Clickable(
                                    tag = "URL",
                                    linkInteractionListener = {
                                        onUiAction(MeetingNoticeDetailUiAction.OnClickCommentWebLink(uiModel.content))
                                    },
                                ),
                            start = startIndex,
                            end = startIndex + uiModel.content.length,
                        )
                    }
                }
            }
        }

    MoimText(
        modifier = modifier,
        text = annotatedText,
        singleLine = false,
    )
}

@Composable
private fun CommentOpenGraph(
    openGraph: OpenGraph,
    onUiAction: OnMeetingNoticeDetailUiAction,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MoimTheme.colors.tertiary)
                .onSingleClick(onClick = { onUiAction(MeetingNoticeDetailUiAction.OnClickCommentWebLink(openGraph.url)) }),
    ) {
        NetworkImage(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(120.dp),
            imageUrl = openGraph.imageUrl,
            errorImage = painterResource(R.drawable.ic_empty_image),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
        ) {
            if (openGraph.title.isNullOrBlank().not()) {
                MoimText(
                    text = openGraph.title ?: "",
                    style = MoimTheme.typography.body02.semiBold,
                    color = MoimTheme.colors.text.text01,
                )
                Spacer(Modifier.height(4.dp))
            }
            MoimText(
                text = openGraph.description ?: "",
                style = MoimTheme.typography.body02.regular,
                color = MoimTheme.colors.text.text01,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun MeetingNoticeDetailCommentItemPreview() {
    val comment =
        NoticeComment(
            commentId = "",
            writer =
                Writer(
                    userId = "",
                    nickname = "모닝커피클럽회원",
                    imageUrl = "",
                ),
            content = "공지 확인했습니다. 다음 모임도 기대돼요!",
            commentAt = ZonedDateTime.now(),
        )

    MoimTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(MoimTheme.colors.bg.primary),
        ) {
            MeetingNoticeDetailCommentItem(
                userId = "",
                comment =
                    NoticeCommentUiModel(
                        comment = comment,
                        texts = listOf(NoticeCommentTextUiModel.PlainText(content = "이른 아침, 공지 확인했습니다. 다음 모임도 기대돼요!")),
                    ),
                onUiAction = {},
            )
        }
    }
}
