package com.moim.core.data.datasource.user.remote

import com.moim.core.datamodel.UserResponse

internal interface UserRemoteDataSource {

    suspend fun getUser() : UserResponse

    suspend fun updateUser(profileUrl: String?, nickname: String): UserResponse

    suspend fun deleteUser()

    suspend fun checkedNickname(nickname:String) : Boolean
}