package com.moim.core.data.datasource.user

import com.moim.core.data.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.data.datasource.user.remote.UserRemoteDataSource
import com.moim.core.data.datastore.PreferenceStorage
import com.moim.core.datamodel.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val imageUploadRemoteDataSource: ImageUploadRemoteDataSource,
    private val preferenceStorage: PreferenceStorage
) : UserRepository {

    override fun getUser(): Flow<UserResponse> {
        return preferenceStorage.user
            .onEach { if (it == null) fetchUser().first() }
            .filterNotNull()
    }

    override fun fetchUser(): Flow<UserResponse> = flow {
        emit(remoteDataSource.getUser().also { preferenceStorage.saveUser(it) })
    }

    override fun updateUser(profileUrl: String?, nickname: String): Flow<UserResponse> = flow {
        val uploadUrl = imageUploadRemoteDataSource.uploadImage(url = profileUrl, folderName = "profile")
        emit(remoteDataSource.updateUser(uploadUrl, nickname).also { preferenceStorage.saveUser(it) })
    }

    override fun deleteUser() = flow {
        emit(remoteDataSource.deleteUser())
    }

    override fun checkedNickname(nickname: String) = flow {
        emit(remoteDataSource.checkedNickname(nickname))
    }

    override suspend fun clearMoimStorage() {
        preferenceStorage.clearMoimStorage()
    }
}