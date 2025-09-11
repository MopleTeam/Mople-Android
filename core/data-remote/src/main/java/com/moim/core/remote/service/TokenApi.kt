package com.moim.core.remote.service

import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenApi {

    @POST("token/save")
    suspend fun setFcmToken(@Body params: JsonObject)
}