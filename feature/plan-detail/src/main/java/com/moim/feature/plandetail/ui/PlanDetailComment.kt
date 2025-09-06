package com.moim.feature.plandetail.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.common.util.decimalFormatString
import com.moim.core.common.util.parseDateString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.Comment
import com.moim.core.model.Writer
import com.moim.feature.plandetail.OnPlanDetailUiAction
import com.moim.feature.plandetail.PlanDetailUiAction
import com.moim.feature.plandetail.model.CommentTextUiModel
import com.moim.feature.plandetail.model.CommentUiModel
import java.time.ZonedDateTime

@Composable
fun PlanDetailCommentHeader(
    modifier: Modifier = Modifier,
    commentCount: Int
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MoimText(
            text = stringResource(R.string.plan_detail_comment),
            style = MoimTheme.typography.title03.semiBold,
            color = MoimTheme.colors.gray.gray01
        )

        Spacer(Modifier.weight(1f))

        MoimText(
            text = stringResource(R.string.unit_count, commentCount.decimalFormatString()),
            style = MoimTheme.typography.title03.semiBold,
            color = MoimTheme.colors.gray.gray04
        )
    }
}

@Composable
fun PlanDetailCommentItem(
    modifier: Modifier = Modifier,
    userId: String,
    comment: CommentUiModel,
    onUiAction: OnPlanDetailUiAction
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        NetworkImage(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(32.dp)
                .clip(CircleShape)
                .border(BorderStroke(1.dp, MoimTheme.colors.stroke), CircleShape)
                .onSingleClick {
                    onUiAction(
                        PlanDetailUiAction.OnClickUserProfileImage(
                            imageUrl = comment.comment.writer.imageUrl,
                            userName = comment.comment.writer.nickname
                        )
                    )
                },
            imageUrl = comment.comment.writer.imageUrl,
            errorImage = painterResource(R.drawable.ic_empty_user_logo),
        )

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            CommentHeader(
                userId = userId,
                comment = comment.comment,
                onUiAction = onUiAction
            )
            Spacer(Modifier.height(8.dp))
            CommentText(
                texts = comment.texts,
                onUiAction = onUiAction
            )
            CommentFooter(
                comment = comment.comment,
                onUiAction = onUiAction
            )
        }
    }

    HorizontalDivider(
        thickness = 1.dp,
        color = MoimTheme.colors.stroke
    )
}

@Composable
private fun CommentHeader(
    modifier: Modifier = Modifier,
    userId: String,
    comment: Comment,
    onUiAction: OnPlanDetailUiAction
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MoimText(
            text = comment.writer.nickname,
            style = MoimTheme.typography.body01.semiBold,
            color = MoimTheme.colors.gray.gray01
        )

        Spacer(Modifier.width(8.dp))

        MoimText(
            modifier = Modifier.weight(1f),
            text = comment.commentAt.parseDateString(stringResource(R.string.regex_date_month_day)),
            style = MoimTheme.typography.body02.regular,
            color = MoimTheme.colors.gray.gray04
        )

        MoimIconButton(
            iconRes = R.drawable.ic_more,
            onClick = {
                val uiAction = if (userId == comment.writer.userId) {
                    PlanDetailUiAction.OnShowCommentEditDialog(
                        isShow = true,
                        comment = comment
                    )
                } else {
                    PlanDetailUiAction.OnShowCommentReportDialog(
                        isShow = true,
                        comment = comment
                    )
                }

                onUiAction(uiAction)
            }
        )
    }

}

@Composable
private fun CommentText(
    modifier: Modifier = Modifier,
    texts: List<CommentTextUiModel>,
    onUiAction: OnPlanDetailUiAction
) {
    val text = texts.joinToString("") { it.content }
    val spanStyle = SpanStyle(
        color = MoimTheme.colors.gray.gray03,
        fontFamily = FontFamily(Font(R.font.pretendard_medium, FontWeight.W600)),
        fontWeight = FontWeight.W600,
        textDecoration = TextDecoration.None
    )
    val annotatedText = buildAnnotatedString {
        texts.forEach { uiModel ->
            when (uiModel) {
                is CommentTextUiModel.PlainText -> {
                    withStyle(style = spanStyle) {
                        append(uiModel.content)
                    }
                }

                is CommentTextUiModel.MentionText -> {
                    withStyle(style = spanStyle) {
                        append(uiModel.content)
                    }
                }

                is CommentTextUiModel.HyperLinkText -> {
                    val startIndex = text.indexOf(uiModel.content)

                    withStyle(style = spanStyle.copy(textDecoration = TextDecoration.Underline)) {
                        append(uiModel.content)
                    }
                    addLink(
                        clickable = LinkAnnotation.Clickable(
                            tag = "URL",
                            linkInteractionListener = { onUiAction(PlanDetailUiAction.OnClickCommentWebLink(uiModel.content)) }
                        ),
                        start = startIndex,
                        end = startIndex + uiModel.content.length
                    )
                }
            }
        }
    }

    MoimText(
        modifier = modifier,
        text = annotatedText,
        singleLine = false
    )
}

@Composable
private fun CommentFooter(
    comment: Comment,
    onUiAction: OnPlanDetailUiAction
) {
    val likeResource = if (comment.isLike) {
        R.drawable.ic_thumb_up_fill
    } else {
        R.drawable.ic_thumb_up
    }

    Column {
        Row(
            modifier = Modifier.padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Row(
                modifier = Modifier
                    .defaultMinSize(minWidth = 56.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .onSingleClick {
                        onUiAction(
                            PlanDetailUiAction.OnClickCommentLike(
                                comment = comment
                            )
                        )
                    }
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(likeResource),
                    contentDescription = "",
                    tint = Color.Unspecified
                )

                if (comment.likeCount > 0) {
                    Text(
                        text = comment.likeCount.decimalFormatString(),
                        style = MoimTheme.typography.body02.medium,
                        color = MoimTheme.colors.gray.gray04,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Icon(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .onSingleClick { onUiAction(PlanDetailUiAction.OnClickCommentAddReply(comment)) },
                imageVector = ImageVector.vectorResource(R.drawable.ic_chat_add),
                contentDescription = "",
                tint = Color.Unspecified
            )
        }

        if (comment.replayCount > 0) {
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .onSingleClick {
                        //::TODO click add
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = stringResource(R.string.plan_detail_comment_reply_show, comment.replayCount.decimalFormatString()),
                    style = MoimTheme.typography.body02.semiBold,
                    color = MoimTheme.colors.gray.gray04
                )
            }
        }
    }
}


@Preview
@Composable
private fun PlanDetailCommentItemPreview() {
    val comment = Comment(
        postId = "",
        commentId = "",
        writer = Writer(
            userId = "",
            nickname = "모닝커피클럽회원",
            imageUrl = "",
        ),
        isLike = true,
        likeCount = 1000,
        replayCount = 0,
        content = "이른 아침, 커피 한 잔과 함께 프로젝트를 시작할 수 있어서 즐거웠어요. 다음에 또 뵐 수 있으면 좋겠네요.\n제 인스타도 많이 방문해주세요 https://www.instagram.com",
        commentAt = ZonedDateTime.now()
    )

    MoimTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoimTheme.colors.bg.primary)
        ) {
            PlanDetailCommentItem(
                userId = "",
                comment = CommentUiModel(
                    comment = comment,
                    texts = listOf(
                        CommentTextUiModel.PlainText(
                            content = "이른 아침, "
                        ),
                        CommentTextUiModel.MentionText(
                            content = "@카카루"
                        ),
                        CommentTextUiModel.PlainText(
                            content = "님과 커피 한 잔과 함께 프로젝트를 시작할 수 있어서 즐거웠어요. "
                        ),
                        CommentTextUiModel.MentionText(
                            content = "@바나나에스프레소"
                        ),
                        CommentTextUiModel.PlainText(
                            content = "님도 다음에 뵐 수 있으면 좋겠네요 제 인스타도 많이 방문해주세요! ",
                        ),
                        CommentTextUiModel.HyperLinkText(
                            content = "instagram.com",
                        ),
                    )
                ),
                onUiAction = {}
            )
        }
    }
}