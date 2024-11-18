package com.moim.core.data.service

import com.moim.core.data.model.PlaceResponseContainer
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

internal interface LocationApi {

    @POST("/location/kakao")
    suspend fun getSearchLocation(@Body params: JsonObject): PlaceResponseContainer
}