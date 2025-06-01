package com.moim.feature.plandetail.ui

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.common.util.getDateTimeFormatString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.Comment
import com.moim.feature.plandetail.OnPlanDetailUiAction
import com.moim.feature.plandetail.PlanDetailUiAction
import com.moim.feature.plandetail.model.CommentTextUiModel
import com.moim.feature.plandetail.model.CommentUiModel

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
            text = stringResource(R.string.unit_count, commentCount),
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
                            imageUrl = comment.comment.userImageUrl,
                            userName = comment.comment.userName
                        )
                    )
                },
            imageUrl = comment.comment.userImageUrl,
            errorImage = painterResource(R.drawable.ic_empty_user_logo),
        )

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MoimText(
                    text = comment.comment.userName,
                    style = MoimTheme.typography.body01.semiBold,
                    color = MoimTheme.colors.gray.gray01
                )

                Spacer(Modifier.width(8.dp))

                MoimText(
                    modifier = Modifier.weight(1f),
                    text = getDateTimeFormatString(
                        dateTime = comment.comment.commentAt,
                        pattern = stringResource(R.string.regex_date_month_day)
                    ),
                    style = MoimTheme.typography.body02.regular,
                    color = MoimTheme.colors.gray.gray04
                )

                MoimIconButton(
                    iconRes = R.drawable.ic_more,
                    onClick = {
                        val uiAction = if (userId == comment.comment.userId) {
                            PlanDetailUiAction.OnShowCommentEditDialog(
                                isShow = true,
                                comment = comment.comment
                            )
                        } else {
                            PlanDetailUiAction.OnShowCommentReportDialog(
                                isShow = true,
                                comment = comment.comment
                            )
                        }

                        onUiAction(uiAction)
                    }
                )
            }

            Spacer(Modifier.height(8.dp))
            CommentText(
                texts = comment.texts,
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
private fun CommentText(
    modifier: Modifier = Modifier,
    texts: List<CommentTextUiModel>,
    onUiAction: OnPlanDetailUiAction
) {
    val annotatedText = buildAnnotatedString {
        texts.forEach { uiModel ->
            when (uiModel) {
                is CommentTextUiModel.PlainText -> {
                    withStyle(
                        style = SpanStyle(
                            color = MoimTheme.colors.gray.gray03,
                            fontFamily = FontFamily(Font(R.font.pretendard_medium, FontWeight.W600)),
                            fontWeight = FontWeight.W600,
                            textDecoration = TextDecoration.None
                        )
                    ) {
                        append(uiModel.content)
                    }
                }

                is CommentTextUiModel.HyperLinkText -> {
                    withStyle(
                        style = SpanStyle(
                            color = MoimTheme.colors.primary.primary,
                            fontFamily = FontFamily(Font(R.font.pretendard_medium, FontWeight.W600)),
                            fontWeight = FontWeight.W600,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append(uiModel.content)
                    }
                    addLink(
                        clickable = LinkAnnotation.Clickable(
                            tag = "URL",
                            linkInteractionListener = {
                                onUiAction(PlanDetailUiAction.OnClickCommentWebLink(uiModel.content))
                            }
                        ),
                        start = uiModel.startIndex,
                        end = uiModel.endIndex
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


@Preview
@Composable
private fun PlanDetailCommentItemPreview() {
    val comment = Comment(
        postId = "",
        commentId = "",
        userId = "",
        userName = "모닝커피클럽회원",
        userImageUrl = "",
        content = "이른 아침, 커피 한 잔과 함께 프로젝트를 시작할 수 있어서 즐거웠어요. 다음에 또 뵐 수 있으면 좋겠네요.\n제 인스타도 많이 방문해주세요 https://www.instagram.com",
        commentAt = "2025-03-29 21:23:20",
        isUpdate = false,
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
                            content = "이른 아침, 커피 한 잔과 함께 프로젝트를 시작할 수 있어서 즐거웠어요. 다음에 또 뵐 수 있으면 좋겠네요.\n제 인스타도 많이 방문해주세요! "
                        ),
                        CommentTextUiModel.HyperLinkText(
                            content = "instagram.com",
                            startIndex = 0,
                            endIndex = 0
                        ),
                    )
                ),
                onUiAction = {}
            )
        }
    }
}