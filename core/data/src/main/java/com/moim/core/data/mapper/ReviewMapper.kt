package com.moim.core.data.mapper

import com.moim.core.common.util.parseZonedDateTime
import com.moim.core.datamodel.ReviewImageResponse
import com.moim.core.datamodel.ReviewResponse
import com.moim.core.model.Review
import com.moim.core.model.ReviewImage


fun ReviewResponse.asItem(): Review {
    return Review(
        userId = userId,
        meetingId = meetingId,
        meetingName = meetingName,
        meetingImageUrl = meetingImageUrl,
        postId = postId,
        reviewId = reviewId,
        reviewName = reviewName,
        address = address,
        latitude = latitude,
        longitude = longitude,
        placeName = placeName,
        memberCount = memberCount,
        images = images.map(ReviewImageResponse::asItem),
        reviewAt = reviewAt.parseZonedDateTime()
    )
}

fun ReviewImageResponse.asItem(): ReviewImage {
    return ReviewImage(
        imageId = imageId,
        imageUrl = reviewImageUrl
    )
}
