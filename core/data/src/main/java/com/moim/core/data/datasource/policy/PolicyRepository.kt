package com.moim.core.data.datasource.policy

import com.moim.core.model.ForceUpdateInfo
import kotlinx.coroutines.flow.Flow

interface PolicyRepository {

    fun getForceUpdateInfo() : Flow<ForceUpdateInfo>
}