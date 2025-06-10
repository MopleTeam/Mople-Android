package com.moim.core.network.service

import com.moim.core.datamodel.ForceUpdateResponse
import retrofit2.http.GET

interface PolicyApi {

    @GET("policy/force-update/status")
    suspend fun getForceUpdateInfo() : ForceUpdateResponse
}
