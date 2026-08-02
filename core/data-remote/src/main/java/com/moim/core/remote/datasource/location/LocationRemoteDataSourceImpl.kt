package com.moim.core.remote.datasource.location

import com.moim.core.remote.di.qualifiers.MoimApi
import com.moim.core.remote.model.PlaceResponseContainer
import com.moim.core.remote.util.postJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

internal class LocationRemoteDataSourceImpl @Inject constructor(
    @MoimApi private val client: HttpClient,
) : LocationRemoteDataSource {
    override suspend fun getSearchLocation(params: JsonObject): PlaceResponseContainer = client.postJson("location/kakao", params).body()
}
