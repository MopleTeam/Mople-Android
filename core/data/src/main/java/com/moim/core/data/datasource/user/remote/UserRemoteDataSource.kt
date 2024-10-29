package com.moim.core.data.datasource.user.remote

import com.moim.core.data.model.UserResponse

internal interface UserRemoteDataSource {

    suspend fun getUser() : UserResponse
}