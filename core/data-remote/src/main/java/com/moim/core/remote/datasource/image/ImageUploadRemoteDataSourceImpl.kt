package com.moim.core.remote.datasource.image

import com.moim.core.remote.util.CompressorUtil
import com.moim.core.remote.util.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class ImageUploadRemoteDataSourceImpl @Inject constructor(
    private val imageRemoteDataSource: ImageRemoteDataSource,
    private val compressorUtil: CompressorUtil,
    private val fileUtil: FileUtil,
) : ImageUploadRemoteDataSource {
    override suspend fun uploadImage(
        url: String?,
        folderName: String,
    ): String? =
        withContext(Dispatchers.IO) {
            return@withContext if (url.isNullOrEmpty() || url.startsWith("http")) {
                url
            } else {
                val imageFile = fileUtil.from(url).run { compressorUtil.compressFile(this) }

                imageRemoteDataSource.uploadImage(
                    folderName = folderName,
                    file = imageFile,
                )
            }
        }

    override suspend fun uploadReviewImages(
        reviewId: String,
        urls: List<String>,
        folderName: String,
    ) {
        val localImageUrls = urls.filterNot { url -> url.isEmpty() || url.startsWith("http") }
        if (localImageUrls.isEmpty()) return

        val imageFiles = localImageUrls.map { fileUtil.from(it).run { compressorUtil.compressFile(this) } }

        imageRemoteDataSource.uploadReviewImages(
            folderName = folderName,
            reviewId = reviewId,
            files = imageFiles,
        )
    }
}
