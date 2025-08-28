package com.moim.feature.plandetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimTextField
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.model.Comment
import com.moim.feature.plandetail.OnPlanDetailUiAction
import com.moim.feature.plandetail.PlanDetailUiAction

@Composable
fun PlanDetailBottomBar(
    modifier: Modifier = Modifier,
    updateComment: Comment? = null,
    onUiAction: OnPlanDetailUiAction = {}
) {
    val commentContent = updateComment?.content ?: ""
    var commentText by remember { mutableStateOf(TextFieldValue(commentContent, TextRange(commentContent.length))) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(updateComment) {
        if (updateComment == null) return@LaunchedEffect
        focusRequester.requestFocus()
        commentText = TextFieldValue(commentContent, TextRange(commentContent.length))
    }

    LaunchedEffect(commentText) {
        if (commentText.text.isEmpty()) {
            onUiAction(PlanDetailUiAction.OnShowMentionDialog(null))
            return@LaunchedEffect
        }
        val textUntilCursor = commentText.text.substring(0, commentText.selection.end)
        val lastAtIndex = textUntilCursor.lastIndexOf("@")

        if (lastAtIndex != -1) {
            val mentionText = textUntilCursor.substring(lastAtIndex + 1)
            onUiAction(PlanDetailUiAction.OnShowMentionDialog(mentionText))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 20.dp)
    ) {
        if (updateComment != null) {
            Text(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MoimTheme.colors.bg.primary)
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                text = stringResource(R.string.plan_detail_comment_update_typing),
                style = MoimTheme.typography.body02.medium,
                color = MoimTheme.colors.gray.gray04
            )
        }

        Box(
            modifier = modifier.fillMaxWidth(),
        ) {
            MoimTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .padding(end = 52.dp)
                    .align(Alignment.CenterStart),
                hintText = stringResource(R.string.plan_detail_comment_hint),
                singleLine = false,
                imeAction = ImeAction.None,
                textFieldValue = commentText,
                onTextChanged = { commentText = it }
            )

            MoimIconButton(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterEnd),
                iconRes = R.drawable.ic_arrow_up,
                backgroundColor = if (commentText.text.isNotEmpty()) MoimTheme.colors.primary.primary else MoimTheme.colors.primary.disable,
                enable = commentText.text.isNotEmpty(),
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onUiAction(PlanDetailUiAction.OnClickCommentUpload(commentText.text, updateComment))
                    commentText = TextFieldValue("")
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanDetailBottomBarPreview() {
    MoimTheme {
        PlanDetailBottomBar()
    }
}