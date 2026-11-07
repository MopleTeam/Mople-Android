package com.moim.core.remote.datasource.token

import kotlinx.serialization.json.JsonObject

interface TokenRemoteDataSource {
    suspend fun setFcmToken(params: JsonObject)
}
