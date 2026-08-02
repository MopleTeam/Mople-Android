package com.moim.core.remote.datasource.location

import com.moim.core.remote.model.PlaceResponseContainer
import kotlinx.serialization.json.JsonObject

interface LocationRemoteDataSource {
    suspend fun getSearchLocation(params: JsonObject): PlaceResponseContainer
}
