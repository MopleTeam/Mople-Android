package com.moim.feature.plandetail.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.common.model.User
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.NetworkImage
import com.moim.core.designsystem.component.onSingleClick
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.feature.plandetail.PlanDetailUiAction

@Composable
fun PlanDetailMentionDialog(
    modifier: Modifier = Modifier,
    userList: List<User>,
    onUiAction: (PlanDetailUiAction) -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(elevation = 12.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MoimTheme.colors.bg.primary),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            items(
                items = userList,
                key = { it.userId },
            ) { user ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .onSingleClick { onUiAction(PlanDetailUiAction.OnClickMentionUser(user)) }
                            .padding(vertical = 8.dp, horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NetworkImage(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(BorderStroke(1.dp, MoimTheme.colors.stroke), CircleShape),
                        imageUrl = user.profileUrl,
                        errorImage = painterResource(R.drawable.ic_empty_user_logo),
                    )

                    Spacer(Modifier.width(8.dp))

                    MoimText(
                        text = user.nickname,
                        style = MoimTheme.typography.body01.regular,
                        color = MoimTheme.colors.gray.gray02,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PlanDetailMentionDialogPreview() {
    MoimTheme {
        listOf(
            User(
                userId = "0",
                nickname = "우리중퉁퉁이",
            ),
        )

        val sample =
            (0..5).map {
                User(
                    userId = it.toString(),
                    nickname = "우리중퉁퉁이_$it",
                )
            }

        Column(
            modifier =
                Modifier
                    .background(MoimTheme.colors.bg.primary)
                    .padding(24.dp),
        ) {
            PlanDetailMentionDialog(
                userList = sample,
                onUiAction = {},
            )
        }
    }
}
