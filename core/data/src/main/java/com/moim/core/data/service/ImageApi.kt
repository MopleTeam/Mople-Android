package com.moim.core.data.service

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

internal interface ImageApi {

    @Multipart
    @POST("image/upload/{folder}")
    suspend fun uploadImage(
        @Path("folder") folderName:String,
        @Part file: MultipartBody.Part
    ) : String
}