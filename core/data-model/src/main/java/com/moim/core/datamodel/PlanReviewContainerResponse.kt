package com.moim.core.datamodel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlanReviewContainerResponse(
    @SerialName("plans")
    val plans: List<PlanResponse>,
    @SerialName("reviews")
    val reviews: List<ReviewResponse>
)