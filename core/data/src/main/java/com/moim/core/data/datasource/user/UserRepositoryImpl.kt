package com.moim.core.data.datasource.user

import com.moim.core.data.datasource.user.remote.UserRemoteDataSource
import com.moim.core.data.datastore.PreferenceStorage
import com.moim.core.data.model.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val preferenceStorage: PreferenceStorage
) : UserRepository {

    override fun getUser(): Flow<UserResponse> = flow {
        emit(remoteDataSource.getUser().also { preferenceStorage.saveUser(it) })
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