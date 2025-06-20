package com.moim.core.network.service

import com.moim.core.datamodel.PlaceResponseContainer
import kotlinx.serialization.json.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

interface LocationApi {

    @POST("location/kakao")
    suspend fun getSearchLocation(@Body params: JsonObject): PlaceResponseContainer
}