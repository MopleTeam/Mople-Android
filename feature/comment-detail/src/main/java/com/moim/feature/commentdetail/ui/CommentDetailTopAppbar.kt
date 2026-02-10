package com.moim.feature.commentdetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.moim.core.designsystem.R
import com.moim.core.designsystem.component.MoimTopAppbar
import com.moim.feature.commentdetail.CommentDetailUiAction

@Composable
fun CommentDetailTopAppbar(
    modifier: Modifier = Modifier,
    onUiAction: (CommentDetailUiAction) -> Unit,
) {
    MoimTopAppbar(
        modifier = modifier,
        title = stringResource(R.string.comment_detail_title),
        onClickNavigate = { onUiAction(CommentDetailUiAction.OnClickBack) },
    )
}
