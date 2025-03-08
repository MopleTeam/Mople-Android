package com.moim.core.data.datasource.image

internal interface ImageUploadRemoteDataSource {

    suspend fun uploadImage(url: String?, folderName: String): String?

    suspend fun uploadReviewImages(reviewId: String, urls: List<String>, folderName: String)
}