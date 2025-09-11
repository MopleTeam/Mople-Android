package com.moim.core.data.datasource.user

import com.moim.core.common.model.User
import com.moim.core.common.util.JsonUtil.jsonOf
import com.moim.core.data.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.data.util.catchFlow
import com.moim.core.local.PreferenceStorage
import com.moim.core.remote.model.asItem
import com.moim.core.remote.service.UserApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val imageUploadRemoteDataSource: ImageUploadRemoteDataSource,
    private val preferenceStorage: PreferenceStorage
) : UserRepository {

    override fun getUser(): Flow<User> {
        return preferenceStorage.user
            .onEach { if (it == null) fetchUser().first() }
            .filterNotNull()
    }

    override fun fetchUser(): Flow<User> = catchFlow {
        emit(userApi.getUser().asItem().also { preferenceStorage.saveUser(it) })
    }

    override fun updateUser(profileUrl: String?, nickname: String): Flow<User> = catchFlow {
        val uploadImageUrl = imageUploadRemoteDataSource.uploadImage(url = profileUrl, folderName = "profile")
        emit(
            userApi.updateUser(
                jsonOf(
                    KEY_IMAGE to uploadImageUrl,
                    KEY_NICKNAME to nickname
                )
            ).asItem().also { preferenceStorage.saveUser(it) }
        )
    }

    override fun deleteUser() = catchFlow {
        emit(userApi.deleteUser())
    }

    override fun checkedNickname(nickname: String) = catchFlow {
        emit(userApi.checkedNickname(nickname))
    }

    override suspend fun clearMoimStorage() {
        preferenceStorage.clearMoimStorage()
    }

    companion object {
        private const val KEY_NICKNAME = "nickname"
        private const val KEY_IMAGE = "image"
    }
}