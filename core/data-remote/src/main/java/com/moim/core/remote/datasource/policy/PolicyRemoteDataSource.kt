package com.moim.core.remote.datasource.policy

import com.moim.core.remote.model.ForceUpdateResponse

interface PolicyRemoteDataSource {
    suspend fun getForceUpdateInfo(): ForceUpdateResponse
}
