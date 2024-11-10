package com.moim.core.data.datasource.image

internal interface ImageUploadRemoteDataSource {

    suspend fun uploadImage(url: String?, folderName: String): String?
}