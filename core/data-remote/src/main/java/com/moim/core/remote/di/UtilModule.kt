package com.moim.core.remote.di

import android.content.Context
import com.moim.core.remote.util.CompressorUtil
import com.moim.core.remote.util.FileUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object UtilModule {
    @Singleton
    @Provides
    fun provideCompressorUtil(
        @ApplicationContext context: Context,
    ): CompressorUtil = CompressorUtil(context)

    @Singleton
    @Provides
    fun provideFileUtil(
        @ApplicationContext context: Context,
    ): FileUtil = FileUtil(context)
}
