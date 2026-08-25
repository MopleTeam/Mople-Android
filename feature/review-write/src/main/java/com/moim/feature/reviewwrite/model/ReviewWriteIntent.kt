package com.moim.feature.reviewwrite.model

import com.moim.core.common.model.ReviewImage
import com.moim.core.ui.mvi.Intent

sealed interface ReviewWriteIntent : Intent {
    data object BackClick : ReviewWriteIntent

    data object RefreshClick : ReviewWriteIntent

    data object ImageUploadClick : ReviewWriteIntent

    data object ParticipantsClick : ReviewWriteIntent

    data object SubmitClick : ReviewWriteIntent

    data class ImagesAdd(
        val imageUrls: List<String>,
    ) : ReviewWriteIntent

    data class ImageRemoveClick(
        val reviewImage: ReviewImage,
    ) : ReviewWriteIntent
}
