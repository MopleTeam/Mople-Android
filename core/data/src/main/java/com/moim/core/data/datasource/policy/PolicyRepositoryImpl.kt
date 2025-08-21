package com.moim.core.data.datasource.policy

import com.moim.core.data.mapper.asItem
import com.moim.core.data.util.catchFlow
import com.moim.core.model.ForceUpdateInfo
import com.moim.core.network.service.PolicyApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class PolicyRepositoryImpl @Inject constructor(
    private val policyApi: PolicyApi
) : PolicyRepository {

    override fun getForceUpdateInfo(): Flow<ForceUpdateInfo> = catchFlow {
        emit(policyApi.getForceUpdateInfo().asItem())
    }
}