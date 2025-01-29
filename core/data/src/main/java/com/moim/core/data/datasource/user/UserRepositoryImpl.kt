package com.moim.core.data.datasource.user

import com.moim.core.data.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.data.datasource.user.remote.UserRemoteDataSource
import com.moim.core.data.datastore.PreferenceStorage
import com.moim.core.model.User
import com.moim.core.model.asItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val imageUploadRemoteDataSource: ImageUploadRemoteDataSource,
    private val preferenceStorage: PreferenceStorage
) : UserRepository {

    override fun getUser(): Flow<User> {
        return preferenceStorage.user
            .onEach { if (it == null) fetchUser().first() }
            .map { it?.asItem() }
            .filterNotNull()
    }

    override fun fetchUser(): Flow<User> = flow {
        emit(remoteDataSource.getUser().also { preferenceStorage.saveUser(it) }.asItem())
    }

    override fun updateUser(profileUrl: String?, nickname: String): Flow<User> = flow {
        val uploadImageUrl = imageUploadRemoteDataSource.uploadImage(url = profileUrl, folderName = "profile")
        emit(remoteDataSource.updateUser(uploadImageUrl, nickname).also { preferenceStorage.saveUser(it) }.asItem())
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