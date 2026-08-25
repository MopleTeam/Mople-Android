package com.moim.feature.reviewwrite.model

import androidx.compose.runtime.Immutable
import com.moim.core.common.model.Review
import com.moim.core.common.model.ReviewImage
import com.moim.core.common.result.Result

@Immutable
data class ReviewWriteState(
    val review: Result<Review> = Result.Loading,
    val isUpdated: Boolean = false,
    val uploadImages: List<ReviewImage> = emptyList(),
    val removeImageIds: List<String> = emptyList(),
    val enableSubmit: Boolean = false,
) {
    val isSuccess
        get() = review is Result.Success

    val isError
        get() = review is Result.Error

    val isLoading
        get() = review is Result.Loading
}
