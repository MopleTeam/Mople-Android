package com.moim.core.data.datasource.policy

import com.moim.core.common.model.ForceUpdateInfo
import com.moim.core.remote.datasource.policy.PolicyRemoteDataSource
import com.moim.core.remote.model.asItem
import javax.inject.Inject

internal class PolicyRepositoryImpl @Inject constructor(
    private val policyRemoteDataSource: PolicyRemoteDataSource,
) : PolicyRepository {
    override suspend fun getForceUpdateInfo(): ForceUpdateInfo = policyRemoteDataSource.getForceUpdateInfo().asItem()
}
