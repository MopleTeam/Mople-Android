package com.moim.core.data.datasource.user.remote

import com.moim.core.data.model.UserResponse
import com.moim.core.data.service.UserApi
import javax.inject.Inject

internal class UserRemoteDataSourceImpl @Inject constructor(
    private val userApi: UserApi,
) : UserRemoteDataSource {

    override suspend fun getUser(): UserResponse {
        return userApi.getUser()
    }
}