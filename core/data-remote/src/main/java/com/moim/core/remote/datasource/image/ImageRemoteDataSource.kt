package com.moim.core.remote.datasource.image

import java.io.File

interface ImageRemoteDataSource {
    suspend fun uploadImage(
        folderName: String,
        file: File,
    ): String

    suspend fun uploadReviewImages(
        folderName: String,
        reviewId: String,
        files: List<File>,
    )
}
