package com.moim.core.common.model.item

import com.moim.core.common.model.Plan
import com.moim.core.common.model.Review
import com.moim.core.common.model.ReviewImage
import com.moim.core.common.model.util.KZonedDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
data class PlanItem(
    val isPlanAtBefore: Boolean = true, // false면 후기(review)
    val userId: String = "",
    val postId: String = "",
    val commentCheckId: String = "",
    val meetingId: String = "",
    val meetingName: String = "",
    val meetingImageUrl: String = "",
    val planName: String = "",
    val participantsCount: Int = 1,
    val loadAddress: String = "",
    val weatherAddress: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val placeName: String = "",
    val weatherIconUrl: String = "",
    val temperature: Float = 0f,
    val isParticipant: Boolean = true,
    val commentCount: Int = 0,
    val reviewImages: List<ReviewImage> = emptyList(),
    @Serializable(KZonedDateTimeSerializer::class)
    val planAt: ZonedDateTime = ZonedDateTime.now(),
) {
    var isDeleted: Boolean = false
}

fun PlanItem.asPlan(): Plan =
    Plan(
        userId = userId,
        meetingId = meetingId,
        meetingName = meetingName,
        meetingImageUrl = meetingImageUrl,
        planId = postId,
        planName = planName,
        planMemberCount = participantsCount,
        planAt = planAt,
        planAddress = loadAddress,
        weatherAddress = weatherAddress,
        planLongitude = longitude,
        planLatitude = latitude,
        placeName = placeName,
        description = description,
        weatherIconUrl = weatherIconUrl,
        isParticipant = isParticipant,
        temperature = temperature,
        commentCount = commentCount,
    )

fun PlanItem.asReview(): Review =
    Review(
        userId = userId,
        meetingId = meetingId,
        meetingName = meetingName,
        meetingImageUrl = meetingImageUrl,
        postId = commentCheckId,
        reviewId = postId,
        reviewName = planName,
        memberCount = participantsCount,
        reviewAt = planAt,
        address = loadAddress,
        longitude = longitude,
        latitude = latitude,
        placeName = placeName,
        description = description,
        images = reviewImages,
        commentCount = commentCount,
    )

fun Plan.asPlanItem(): PlanItem =
    PlanItem(
        isPlanAtBefore = true,
        userId = userId,
        postId = planId,
        commentCheckId = planId,
        meetingId = meetingId,
        meetingName = meetingName,
        meetingImageUrl = meetingImageUrl,
        planName = planName,
        participantsCount = planMemberCount,
        planAt = planAt,
        loadAddress = planAddress,
        latitude = planLatitude,
        longitude = planLongitude,
        placeName = placeName,
        description = description,
        temperature = temperature,
        weatherAddress = weatherAddress,
        weatherIconUrl = weatherIconUrl,
        isParticipant = isParticipant,
        reviewImages = emptyList(),
        commentCount = commentCount,
    )

fun Review.asPlanItem(): PlanItem =
    PlanItem(
        isPlanAtBefore = false,
        userId = userId,
        postId = reviewId,
        commentCheckId = postId,
        meetingId = meetingId,
        meetingName = meetingName,
        meetingImageUrl = meetingImageUrl,
        planName = reviewName,
        participantsCount = memberCount,
        planAt = reviewAt,
        loadAddress = address,
        latitude = latitude,
        longitude = longitude,
        placeName = placeName,
        temperature = temperature,
        weatherAddress = weatherAddress,
        weatherIconUrl = weatherIcon,
        description = description,
        reviewImages = images,
        commentCount = commentCount,
    )
