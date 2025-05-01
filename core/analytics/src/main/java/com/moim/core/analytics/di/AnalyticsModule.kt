package com.moim.core.analytics.di

import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.moim.core.analytics.AnalyticsHelper
import com.moim.core.analytics.FirebaseAnalyticsHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AnalyticsModule {
    @Binds
    abstract fun bindsAnalyticsHelper(analyticsHelperImpl: FirebaseAnalyticsHelper): AnalyticsHelper

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics
    }
}
