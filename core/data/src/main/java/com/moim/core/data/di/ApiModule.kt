package com.moim.core.data.di

import com.moim.core.data.di.qualifiers.MoimApi
import com.moim.core.data.di.qualifiers.NormalApi
import com.moim.core.data.service.AuthApi
import com.moim.core.data.service.ImageApi
import com.moim.core.data.service.LocationApi
import com.moim.core.data.service.MeetingApi
import com.moim.core.data.service.PlanApi
import com.moim.core.data.service.ReviewApi
import com.moim.core.data.service.UserApi
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
    ) : ReviewApi = retrofit.create(ReviewApi::class.java)

    @Provides
    @Singleton
    fun provideImageApi(
        @MoimApi retrofit: Retrofit
    ): ImageApi = retrofit.create(ImageApi::class.java)

    @Provides
    @Singleton
    fun provideLocationApi(
        @MoimApi retrofit: Retrofit
    ): LocationApi = retrofit.create(LocationApi::class.java)
}