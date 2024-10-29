package com.moim.core.data.di

import com.moim.core.data.datasource.auth.AuthRepository
import com.moim.core.data.datasource.auth.AuthRepositoryImpl
import com.moim.core.data.datasource.auth.remote.AuthRemoteDataSource
import com.moim.core.data.datasource.auth.remote.AuthRemoteDataSourceImpl
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.data.datasource.user.UserRepositoryImpl
import com.moim.core.data.datasource.user.remote.UserRemoteDataSource
import com.moim.core.data.datasource.user.remote.UserRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal abstract class DataModule {

    //================================ Auth ============================================//
    @Singleton
    @Binds
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    abstract fun bindAuthRemoteDataSource(authRemoteDataSourceImpl: AuthRemoteDataSourceImpl): AuthRemoteDataSource

    //================================ User ============================================//
    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindUserRemoteDataSource(userRemoteDataSourceImpl: UserRemoteDataSourceImpl): UserRemoteDataSource
}