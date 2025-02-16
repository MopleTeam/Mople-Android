package com.moim.core.model

import androidx.compose.runtime.Stable
import com.moim.core.datamodel.PlanResponse
import com.moim.core.datamodel.PlanReviewContainerResponse
import com.moim.core.datamodel.ReviewResponse
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class PlanReviewContainer(
    val plans: List<Plan>,
    val reviews: List<Review>
)

fun PlanReviewContainerResponse.asItem(): PlanReviewContainer {
    return PlanReviewContainer(
        plans = plans.map(PlanResponse::asItem),
        reviews = reviews.map(ReviewResponse::asItem)
    )
}