package com.moim.core.data.datasource.user

import com.moim.core.data.model.UserResponse
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUser() : Flow<UserResponse>

    fun checkedNickname(nickname:String) : Flow<Boolean>

    suspend fun clearMoimStorage()
}