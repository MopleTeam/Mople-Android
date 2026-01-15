package com.moim.core.remote.model

import com.moim.core.common.model.PlanReviewContainer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlanReviewContainerResponse(
    @SerialName("plans")
    val plans: List<PlanResponse>,
    @SerialName("reviews")
    val reviews: List<ReviewResponse>,
)

fun PlanReviewContainerResponse.asItem(): PlanReviewContainer =
    PlanReviewContainer(
        plans = plans.map(PlanResponse::asItem),
        reviews = reviews.map(ReviewResponse::asItem),
    )
