package com.moim.core.data.datasource.image

import com.moim.core.data.service.ImageApi
import com.moim.core.data.util.CompressorUtil
import com.moim.core.data.util.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.net.URLEncoder
import javax.inject.Inject

internal class ImageUploadRemoteDataSourceImpl @Inject constructor(
    private val imageApi: ImageApi,
    private val compressorUtil: CompressorUtil,
    private val fileUtil: FileUtil,
) : ImageUploadRemoteDataSource {

    override suspend fun uploadImage(url: String?, folderName: String): String? = withContext(Dispatchers.IO) {
        return@withContext if (url.isNullOrEmpty() || url.startsWith("http")) {
            url
        } else {
            val imageFile = fileUtil.from(url).run { compressorUtil.compressFile(this) }

            imageApi.uploadImage(
                folderName = folderName,
                file = MultipartBody.Part.createFormData(
                    name = "image",
                    filename = URLEncoder.encode(imageFile.name, Charsets.UTF_8.displayName()),
                    body = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                )
            )
        }
    }
}