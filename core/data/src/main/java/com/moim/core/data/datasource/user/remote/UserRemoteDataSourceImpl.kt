package com.moim.core.data.datasource.user.remote

import com.moim.core.data.model.UserResponse
import com.moim.core.data.service.UserApi
import com.moim.core.data.util.converterException
import javax.inject.Inject

internal class UserRemoteDataSourceImpl @Inject constructor(
    private val userApi: UserApi,
) : UserRemoteDataSource {

    override suspend fun getUser(): UserResponse {
        return try {
            userApi.getUser()
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun deleteUser() {
        return try {
            userApi.deleteUser()
        } catch (e:Exception) {
            throw converterException(e)
        }
    }

    override suspend fun checkedNickname(nickname: String): Boolean {
        return try {
            userApi.checkedNickname(nickname)
        } catch (e: Exception) {
            throw converterException(e)
        }
    }
}