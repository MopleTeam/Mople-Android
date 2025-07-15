package com.moim.core.model

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class PlanReviewContainer(
    val plans: List<Plan>,
    val reviews: List<Review>
)
