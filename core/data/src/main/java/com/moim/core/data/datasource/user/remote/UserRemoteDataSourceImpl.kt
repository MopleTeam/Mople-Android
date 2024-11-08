package com.moim.core.data.datasource.user.remote

import com.moim.core.data.model.UserResponse
import com.moim.core.data.service.UserApi
import com.moim.core.data.util.JsonUtil.jsonOf
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

    override suspend fun updateUser(profileUrl: String?, nickname: String): UserResponse {
        return try {
            userApi.updateUser(
                jsonOf(
                    KEY_IMAGE to profileUrl,
                    KEY_NICKNAME to nickname
                )
            )
        } catch (e: Exception) {
            throw converterException(e)
        }
    }

    override suspend fun deleteUser() {
        return try {
            userApi.deleteUser()
        } catch (e: Exception) {
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

    companion object {
        private const val KEY_NICKNAME = "nickName"
        private const val KEY_IMAGE = "image"
    }
}