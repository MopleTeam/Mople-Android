package com.moim.core.remote.di

import com.moim.core.remote.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.remote.datasource.image.ImageUploadRemoteDataSourceImpl
import com.moim.core.remote.datasource.opengraph.OpenGraphRemoteDataSource
import com.moim.core.remote.datasource.opengraph.OpenGraphRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
internal abstract class DataModule {

    //================================ OpenGraph ============================================//
    @Singleton
    @Binds
    abstract fun bindOpenGraphRemoteDataSource(openGraphRemoteDataSourceImpl: OpenGraphRemoteDataSourceImpl): OpenGraphRemoteDataSource

    //================================ Image ============================================//
    @Singleton
    @Binds
    abstract fun bindImageUploadRemoteDataSource(imageUploadRemoteDataSource: ImageUploadRemoteDataSourceImpl): ImageUploadRemoteDataSource

}