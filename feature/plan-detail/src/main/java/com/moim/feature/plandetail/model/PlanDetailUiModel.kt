package com.moim.feature.plandetail.model

import com.moim.core.model.Plan
import com.moim.core.model.Review

data class PlanDetailUiModel(
    val userId: String = "",
    val postId: String = "",
    val meetingId: String = "",
    val meetingName: String = "",
    val meetingImageUrl: String = "",
    val planName: String = "",
    val participantsCount: Int = 1,
    val planAt: String = "",
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val isPlan: Boolean = true,
    val images: List<String> = emptyList(),
)

fun Plan.createDetailUiModel(): PlanDetailUiModel {
    return PlanDetailUiModel(
        userId = this.userId,
        postId = this.planId,
        meetingId = this.meetingId,
        meetingName = this.meetingName,
        meetingImageUrl = this.meetingImageUrl,
        planName = this.planName,
        participantsCount = this.planMemberCount,
        planAt = this.planTime,
        address = this.planAddress,
        lat = this.planLatitude,
        lng = this.planLongitude,
        isPlan = true,
        images = emptyList()
    )
}

fun Review.createDetailUiModel(): PlanDetailUiModel {
    return PlanDetailUiModel(
        userId = this.userId,
        postId = this.postId,
        meetingId = this.meetingId,
        meetingName = this.meetingName,
        meetingImageUrl = this.meetingImageUrl,
        planName = this.reviewName,
        participantsCount = this.memberCount,
        planAt = this.reviewAt,
        address = this.address,
        lat = this.latitude,
        lng = this.longitude,
        isPlan = false,
        images = this.images
    )
}