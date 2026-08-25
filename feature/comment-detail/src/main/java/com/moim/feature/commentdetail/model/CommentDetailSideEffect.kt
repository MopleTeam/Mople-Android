package com.moim.feature.commentdetail.model

import com.moim.core.ui.view.ToastMessage

sealed interface CommentDetailSideEffect {
    data object NavigateToBack : CommentDetailSideEffect

    data class NavigateToImageViewerForUser(
        val image: String,
        val userName: String,
    ) : CommentDetailSideEffect

    data class NavigateToWebBrowser(
        val webLink: String,
    ) : CommentDetailSideEffect

    data class ShowToastMessage(
        val message: ToastMessage,
    ) : CommentDetailSideEffect
}
