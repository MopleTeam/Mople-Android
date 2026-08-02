package com.moim.core.remote.datasource.policy

import com.moim.core.remote.di.qualifiers.NormalApi
import com.moim.core.remote.model.ForceUpdateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

internal class PolicyRemoteDataSourceImpl @Inject constructor(
    @NormalApi private val client: HttpClient,
) : PolicyRemoteDataSource {
    override suspend fun getForceUpdateInfo(): ForceUpdateResponse = client.get("policy/force-update/status").body()
}
