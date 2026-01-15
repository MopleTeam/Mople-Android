package com.moim.core.remote.di

import com.moim.core.remote.di.qualifiers.MoimApi
import com.moim.core.remote.di.qualifiers.NormalApi
import com.moim.core.remote.service.AuthApi
import com.moim.core.remote.service.CommentApi
import com.moim.core.remote.service.HolidayApi
import com.moim.core.remote.service.ImageApi
import com.moim.core.remote.service.LocationApi
import com.moim.core.remote.service.MeetingApi
import com.moim.core.remote.service.NotificationApi
import com.moim.core.remote.service.PlanApi
import com.moim.core.remote.service.PolicyApi
import com.moim.core.remote.service.ReviewApi
import com.moim.core.remote.service.TokenApi
import com.moim.core.remote.service.UserApi
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
        @NormalApi retrofit: Retrofit,
    ): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun providePolicyApi(
        @NormalApi retrofit: Retrofit,
    ): PolicyApi = retrofit.create(PolicyApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(
        @MoimApi retrofit: Retrofit,
    ): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideMeetingApi(
        @MoimApi retrofit: Retrofit,
    ): MeetingApi = retrofit.create(MeetingApi::class.java)

    @Provides
    @Singleton
    fun providePlanApi(
        @MoimApi retrofit: Retrofit,
    ): PlanApi = retrofit.create(PlanApi::class.java)

    @Provides
    @Singleton
    fun provideReviewApi(
        @MoimApi retrofit: Retrofit,
    ): ReviewApi = retrofit.create(ReviewApi::class.java)

    @Provides
    @Singleton
    fun provideImageApi(
        @MoimApi retrofit: Retrofit,
    ): ImageApi = retrofit.create(ImageApi::class.java)

    @Provides
    @Singleton
    fun provideCommentApi(
        @MoimApi retrofit: Retrofit,
    ): CommentApi = retrofit.create(CommentApi::class.java)

    @Provides
    @Singleton
    fun provideNotificationApi(
        @MoimApi retrofit: Retrofit,
    ): NotificationApi = retrofit.create(NotificationApi::class.java)

    @Provides
    @Singleton
    fun provideTokenApi(
        @MoimApi retrofit: Retrofit,
    ): TokenApi = retrofit.create(TokenApi::class.java)

    @Provides
    @Singleton
    fun provideLocationApi(
        @MoimApi retrofit: Retrofit,
    ): LocationApi = retrofit.create(LocationApi::class.java)

    @Provides
    @Singleton
    fun provideHolidayApi(
        @MoimApi retrofit: Retrofit,
    ): HolidayApi = retrofit.create(HolidayApi::class.java)
}
