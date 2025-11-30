package com.moim.core.remote.datasource.image

interface ImageUploadRemoteDataSource {

    suspend fun uploadImage(url: String?, folderName: String): String?

    suspend fun uploadReviewImages(reviewId: String, urls: List<String>, folderName: String)
}