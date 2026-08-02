package com.moim.core.remote.datasource.review

import com.moim.core.remote.di.qualifiers.MoimApi
import com.moim.core.remote.model.PaginationContainerResponse
import com.moim.core.remote.model.ReviewResponse
import com.moim.core.remote.model.UserResponse
import com.moim.core.remote.util.deleteJson
import com.moim.core.remote.util.postJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

internal class ReviewRemoteDataSourceImpl @Inject constructor(
    @MoimApi private val client: HttpClient,
) : ReviewRemoteDataSource {
    override suspend fun getReviews(
        id: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<ReviewResponse>> =
        client
            .get("review/list/$id") {
                parameter("cursor", cursor)
                parameter("size", size)
            }.body()

    override suspend fun getReview(id: String): ReviewResponse = client.get("review/$id").body()

    override suspend fun gerReviewForPostId(id: String): ReviewResponse = client.get("review/post/$id").body()

    override suspend fun getReviewParticipant(
        id: String,
        cursor: String,
        size: Int,
    ): PaginationContainerResponse<List<UserResponse>> =
        client
            .get("review/participants/$id") {
                parameter("cursor", cursor)
                parameter("size", size)
            }.body()

    override suspend fun deleteReviewImage(
        id: String,
        params: JsonObject,
    ) {
        client.deleteJson("review/images/$id", params)
    }

    override suspend fun deleteReview(id: String) {
        client.delete("review/$id")
    }

    override suspend fun reportReview(params: JsonObject) {
        client.postJson("review/report", params)
    }
}
