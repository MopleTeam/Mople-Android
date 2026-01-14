package com.moim.core.local.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.moim.core.local.PreferenceStorageImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {
    private val Context.preference: DataStore<Preferences> by preferencesDataStore(
        name = PreferenceStorageImpl.PREFS_MOIM,
    )

    @Provides
    @Singleton
    fun providesDataStoreModule(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.preference
}
