package com.moim.core.data.datasource.policy

import com.moim.core.common.model.ForceUpdateInfo
import com.moim.core.remote.datasource.policy.PolicyRemoteDataSource
import com.moim.core.remote.model.asItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class PolicyRepositoryImpl @Inject constructor(
    private val policyRemoteDataSource: PolicyRemoteDataSource,
) : PolicyRepository {
    override fun getForceUpdateInfo(): Flow<ForceUpdateInfo> =
        flow {
            emit(policyRemoteDataSource.getForceUpdateInfo().asItem())
        }
}
