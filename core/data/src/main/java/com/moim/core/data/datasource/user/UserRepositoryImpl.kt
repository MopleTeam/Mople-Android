package com.moim.core.data.datasource.user

import com.moim.core.data.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.data.datastore.PreferenceStorage
import com.moim.core.data.service.UserApi
import com.moim.core.data.util.JsonUtil.jsonOf
import com.moim.core.data.util.catchFlow
import com.moim.core.model.User
import com.moim.core.model.asItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
            .map { it?.asItem() }
            .filterNotNull()
    }

    override fun fetchUser(): Flow<User> = catchFlow {
        emit(userApi.getUser().also { preferenceStorage.saveUser(it) }.asItem())
    }

    override fun updateUser(profileUrl: String?, nickname: String): Flow<User> = catchFlow {
        val uploadImageUrl = imageUploadRemoteDataSource.uploadImage(url = profileUrl, folderName = "profile")
        emit(
            userApi.updateUser(
                jsonOf(
                    KEY_IMAGE to uploadImageUrl,
                    KEY_NICKNAME to nickname
                )
            ).also { preferenceStorage.saveUser(it) }.asItem()
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