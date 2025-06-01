package com.moim.core.network.di

import com.moim.core.network.di.qualifiers.MoimApi
import com.moim.core.network.di.qualifiers.NormalApi
import com.moim.core.network.service.AuthApi
import com.moim.core.network.service.CommentApi
import com.moim.core.network.service.ImageApi
import com.moim.core.network.service.LocationApi
import com.moim.core.network.service.MeetingApi
import com.moim.core.network.service.NotificationApi
import com.moim.core.network.service.PlanApi
import com.moim.core.network.service.ReviewApi
import com.moim.core.network.service.TokenApi
import com.moim.core.network.service.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object ApiModule {

    @Provides
    @Singleton
    fun provideAuthApi(
        @NormalApi retrofit: Retrofit
    ): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(
        @MoimApi retrofit: Retrofit
    ): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideMeetingApi(
        @MoimApi retrofit: Retrofit
    ): MeetingApi = retrofit.create(MeetingApi::class.java)

    @Provides
    @Singleton
    fun providePlanApi(
        @MoimApi retrofit: Retrofit
    ): PlanApi = retrofit.create(PlanApi::class.java)

    @Provides
    @Singleton
    fun provideReviewApi(
        @MoimApi retrofit: Retrofit
    ): ReviewApi = retrofit.create(ReviewApi::class.java)

    @Provides
    @Singleton
    fun provideImageApi(
        @MoimApi retrofit: Retrofit
    ): ImageApi = retrofit.create(ImageApi::class.java)

    @Provides
    @Singleton
    fun provideCommentApi(
        @MoimApi retrofit: Retrofit
    ): CommentApi = retrofit.create(CommentApi::class.java)

    @Provides
    @Singleton
    fun provideNotificationApi(
        @MoimApi retrofit: Retrofit
    ): NotificationApi = retrofit.create(NotificationApi::class.java)

    @Provides
    @Singleton
    fun provideTokenApi(
        @MoimApi retrofit: Retrofit
    ): TokenApi = retrofit.create(TokenApi::class.java)

    @Provides
    @Singleton
    fun provideLocationApi(
        @MoimApi retrofit: Retrofit
    ): LocationApi = retrofit.create(LocationApi::class.java)
}