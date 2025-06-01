package com.moim.core.datastore.di

import com.moim.core.datastore.PreferenceStorage
import com.moim.core.datastore.PreferenceStorageImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PreferenceStorageModule {
    @Binds
    abstract fun bindPreferenceStorage(preferenceStorageImpl: PreferenceStorageImpl) : PreferenceStorage
}