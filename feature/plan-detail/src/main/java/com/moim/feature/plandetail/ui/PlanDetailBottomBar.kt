package com.moim.feature.plandetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimIconButton
import com.moim.core.designsystem.component.MoimText
import com.moim.core.designsystem.component.MoimTextField
import com.moim.core.designsystem.theme.MoimTheme
import com.moim.core.designsystem.theme.moimTextFieldColors
import com.moim.core.model.Comment
import com.moim.core.model.User
import com.moim.feature.plandetail.OnPlanDetailUiAction
import com.moim.feature.plandetail.PlanDetailUiAction

@Composable
fun PlanDetailBottomBar(
    modifier: Modifier = Modifier,
    updateComment: Comment? = null,
    commentState: TextFieldState = TextFieldState(),
    selectedMentions: List<User>,
    onUiAction: OnPlanDetailUiAction = {}
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(updateComment) {
        if (commentState.text.isEmpty()) return@LaunchedEffect
        focusRequester.requestFocus()
    }

    LaunchedEffect(commentState.text) {
        if (commentState.text.isEmpty()) {
            onUiAction(PlanDetailUiAction.OnShowMentionDialog(null))
            return@LaunchedEffect
        }

        val textUntilCursor = commentState.text.toString().substring(0, commentState.selection.end)
        val lastAtIndex = textUntilCursor.lastIndexOf("@")

        if (lastAtIndex != -1) {
            val mentionText = textUntilCursor.substring(lastAtIndex + 1)
            onUiAction(PlanDetailUiAction.OnShowMentionDialog(mentionText))
        } else {
            onUiAction(PlanDetailUiAction.OnShowMentionDialog(null))
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
                textFieldState = commentState,
                textStyle = MoimTheme.typography.body01.regular,
                textFieldColors = moimTextFieldColors().copy(
                    focusedTextColor = MoimTheme.colors.transparent,
                    unfocusedTextColor = MoimTheme.colors.transparent,
                )
            )

            HighlightTextView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(end = 52.dp)
                    .align(Alignment.CenterStart),
                keywords = selectedMentions.map { "@${it.nickname}" },
                currentMessage = commentState.text.toString(),
            )

            MoimIconButton(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterEnd),
                iconRes = R.drawable.ic_arrow_up,
                backgroundColor = if (commentState.text.isNotEmpty()) MoimTheme.colors.primary.primary else MoimTheme.colors.primary.disable,
                enable = commentState.text.isNotEmpty(),
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onUiAction(PlanDetailUiAction.OnClickCommentUpload(updateComment))
                }
            )
        }
    }
}

@Composable
fun HighlightTextView(
    keywords: List<String>,
    currentMessage: String,
    modifier: Modifier = Modifier,
    highlightColor: Color = MoimTheme.colors.primary.primary,
    textColor: Color = MoimTheme.colors.gray.gray01,
) {
    val annotatedString = buildAnnotatedString {
        if (keywords.isEmpty()) {
            append(currentMessage)
        } else {
            val allMatches = mutableListOf<Pair<IntRange, String>>()

            keywords.forEach { keyword ->
                if (keyword.isNotEmpty()) {
                    var startIndex = currentMessage.indexOf(keyword, ignoreCase = true)
                    while (startIndex != -1) {
                        val endIndex = startIndex + keyword.length - 1
                        allMatches.add(IntRange(startIndex, endIndex) to keyword)
                        startIndex = currentMessage.indexOf(keyword, startIndex + 1, ignoreCase = true)
                    }
                }
            }

            val sortedMatches = allMatches
                .sortedBy { it.first.first }
                .fold(mutableListOf<Pair<IntRange, String>>()) { acc, current ->
                    if (acc.isEmpty() || acc.last().first.last < current.first.first) {
                        acc.add(current)
                    } else {
                        val lastMatch = acc.last()
                        if (current.second.length > lastMatch.second.length) {
                            acc[acc.lastIndex] = current
                        }
                    }
                    acc
                }

            var lastIndex = 0

            sortedMatches.forEach { (range, _) ->
                val startIndex = range.first
                val endIndex = range.last + 1

                if (startIndex > lastIndex) {
                    append(currentMessage.substring(lastIndex, startIndex))
                }

                withStyle(style = SpanStyle(color = highlightColor)) {
                    append(currentMessage.substring(startIndex, endIndex))
                }

                lastIndex = endIndex
            }

            if (lastIndex < currentMessage.length) {
                append(currentMessage.substring(lastIndex))
            }
        }
    }

    MoimText(
        modifier = modifier,
        text = annotatedString,
        singleLine = false,
        style = MoimTheme.typography.body01.regular,
        color = textColor,
    )
}


@Preview(showBackground = true)
@Composable
private fun PlanDetailBottomBarPreview() {
    MoimTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MoimTheme.colors.white)
        ) {
            PlanDetailBottomBar(
                commentState = TextFieldState(
                    """
                        hello @kim
                    """.trimIndent()
                ),
                selectedMentions = listOf(
                    User(
                        userId = "0",
                        nickname = "kim"
                    )
                )
            )
        }
    }
}