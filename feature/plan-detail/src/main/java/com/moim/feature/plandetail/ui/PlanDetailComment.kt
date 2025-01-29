package com.moim.feature.plandetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.common.util.getDateTimeFormatString
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.Comment
import com.moim.feature.plandetail.OnPlanDetailUiAction
import com.moim.feature.plandetail.PlanDetailUiAction

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
    comment: Comment,
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
                .clip(RoundedCornerShape(100)),
            imageUrl = comment.userImageUrl,
            errorImage = painterResource(R.drawable.ic_empty_image)
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
                    text = comment.userName,
                    style = MoimTheme.typography.body01.semiBold,
                    color = MoimTheme.colors.gray.gray01
                )

                Spacer(Modifier.width(8.dp))

                MoimText(
                    modifier = Modifier.weight(1f),
                    text = getDateTimeFormatString(
                        dateTime = comment.commentAt,
                        pattern = stringResource(R.string.regex_date_month_day)
                    ),
                    style = MoimTheme.typography.body02.regular,
                    color = MoimTheme.colors.gray.gray04
                )

                MoimIconButton(
                    iconRes = R.drawable.ic_more,
                    onClick = {
                        val uiAction = if (userId == comment.userId) {
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

            Spacer(Modifier.height(8.dp))
            MoimText(
                text = comment.content,
                singleLine = false,
                style = MoimTheme.typography.body01.medium,
                color = MoimTheme.colors.gray.gray03
            )
        }
    }

    HorizontalDivider(
        thickness = 1.dp,
        color = MoimTheme.colors.stroke
    )
}

@Preview
@Composable
private fun PlanDetailCommentItemPreview() {
    MoimTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoimTheme.colors.bg.primary)
        ) {
            PlanDetailCommentItem(
                userId = "",
                comment = Comment(
                    postId = "",
                    commentId = "",
                    userId = "",
                    userName = "Coffee",
                    userImageUrl = "",
                    content = "Hello World",
                    commentAt = "",
                    isUpdate = false,
                ),
                onUiAction = {}
            )
        }
    }
}