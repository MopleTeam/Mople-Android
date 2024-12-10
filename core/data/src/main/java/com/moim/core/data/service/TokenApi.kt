package com.moim.core.data.service

import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

internal interface TokenApi {

    @POST("/token/save")
    suspend fun setFcmToken(@Body params: JsonObject)
}