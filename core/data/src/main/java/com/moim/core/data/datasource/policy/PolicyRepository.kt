package com.moim.core.data.datasource.policy

import com.moim.core.common.model.ForceUpdateInfo

interface PolicyRepository {
    suspend fun getForceUpdateInfo(): ForceUpdateInfo
}
