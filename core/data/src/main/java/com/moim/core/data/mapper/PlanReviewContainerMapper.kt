package com.moim.core.data.mapper

import com.moim.core.datamodel.PlanResponse
import com.moim.core.datamodel.PlanReviewContainerResponse
import com.moim.core.datamodel.ReviewResponse
import com.moim.core.model.PlanReviewContainer

fun PlanReviewContainerResponse.asItem(): PlanReviewContainer {
    return PlanReviewContainer(
        plans = plans.map(PlanResponse::asItem),
        reviews = reviews.map(ReviewResponse::asItem)
    )
}