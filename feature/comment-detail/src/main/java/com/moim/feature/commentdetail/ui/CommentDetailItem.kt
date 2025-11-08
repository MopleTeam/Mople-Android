package com.moim.feature.commentdetail.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.Comment
import com.moim.core.common.model.isChild
import com.moim.core.common.model.item.CommentTextUiModel
import com.moim.core.common.model.item.CommentUiModel
import com.moim.core.ui.util.decimalFormatString
import com.moim.core.common.util.parseDateString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.commentdetail.CommentDetailAction

@Composable
fun CommentDetailItem(
    modifier: Modifier = Modifier,
    userId: String,
    comment: CommentUiModel,
    onUiAction: (CommentDetailAction) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(vertical = if (comment.comment.isChild()) 24.dp else 20.dp)
    ) {
        NetworkImage(
            modifier = Modifier
                .padding(start = if (comment.comment.isChild()) 40.dp else 0.dp)
                .padding(top = 4.dp)
                .size(32.dp)
                .clip(CircleShape)
                .border(BorderStroke(1.dp, MoimTheme.colors.stroke), CircleShape)
                .onSingleClick {
                    onUiAction(
                        CommentDetailAction.OnClickUserProfileImage(
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
}

@Composable
private fun CommentHeader(
    modifier: Modifier = Modifier,
    userId: String,
    comment: Comment,
    onUiAction: (CommentDetailAction) -> Unit
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
                    CommentDetailAction.OnShowCommentEditDialog(
                        isShow = true,
                        comment = comment
                    )
                } else {
                    CommentDetailAction.OnShowCommentReportDialog(
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
    onUiAction: (CommentDetailAction) -> Unit
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
                    withStyle(style = spanStyle.copy(color = MoimTheme.colors.primary.primary)) {
                        append(uiModel.content)
                    }
                }

                is CommentTextUiModel.HyperLinkText -> {
                    val startIndex = text.indexOf(uiModel.content)

                    withStyle(
                        style = spanStyle.copy(
                            color = MoimTheme.colors.primary.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append(uiModel.content)
                    }
                    addLink(
                        clickable = LinkAnnotation.Clickable(
                            tag = "URL",
                            linkInteractionListener = { onUiAction(CommentDetailAction.OnClickCommentWebLink(uiModel.content)) }
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
    onUiAction: (CommentDetailAction) -> Unit
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
            CommentIcon(
                modifier = Modifier.padding(end = 8.dp),
                iconRes = likeResource,
                iconCount = comment.likeCount,
                onClick = { onUiAction(CommentDetailAction.OnClickCommentLike(comment = comment)) }
            )
        }
    }
}


@Composable
private fun CommentIcon(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    iconCount: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .defaultMinSize(minWidth = 56.dp)
            .clip(RoundedCornerShape(4.dp))
            .onSingleClick(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(iconRes),
            contentDescription = "",
            tint = Color.Unspecified
        )

        if (iconCount > 0) {
            Text(
                text = iconCount.decimalFormatString(),
                style = MoimTheme.typography.body02.medium,
                color = MoimTheme.colors.gray.gray04,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
