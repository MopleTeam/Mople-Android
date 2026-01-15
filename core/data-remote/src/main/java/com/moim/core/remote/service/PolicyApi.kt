package com.moim.core.remote.service

import com.moim.core.remote.model.ForceUpdateResponse
import retrofit2.http.GET

interface PolicyApi {
    @GET("policy/force-update/status")
    suspend fun getForceUpdateInfo(): ForceUpdateResponse
}
