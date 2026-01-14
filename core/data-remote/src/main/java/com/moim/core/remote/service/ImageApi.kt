package com.moim.core.remote.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ImageApi {
    @Multipart
    @POST("image/upload/{folder}")
    suspend fun uploadImage(
        @Path("folder") folderName: String,
        @Part file: MultipartBody.Part,
    ): String

    @Multipart
    @POST("image/review/{folder}")
    suspend fun uploadReviewImages(
        @Path("folder") folderName: String,
        @Part("reviewId") reviewId: RequestBody,
        @Part files: List<MultipartBody.Part>,
    )
}
