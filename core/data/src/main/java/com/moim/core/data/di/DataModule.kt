package com.moim.core.data.di

import com.moim.core.data.datasource.auth.AuthRepository
import com.moim.core.data.datasource.auth.AuthRepositoryImpl
import com.moim.core.data.datasource.auth.remote.AuthRemoteDataSource
import com.moim.core.data.datasource.auth.remote.AuthRemoteDataSourceImpl
import com.moim.core.data.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.data.datasource.image.ImageUploadRemoteDataSourceImpl
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.meeting.MeetingRepositoryImpl
import com.moim.core.data.datasource.meeting.remote.MeetingRemoteDataSource
import com.moim.core.data.datasource.meeting.remote.MeetingRemoteDataSourceImpl
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.plan.PlanRepositoryImpl
import com.moim.core.data.datasource.plan.remote.PlanRemoteDataSource
import com.moim.core.data.datasource.plan.remote.PlanRemoteDataSourceImpl
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.data.datasource.review.ReviewRepositoryImpl
import com.moim.core.data.datasource.review.remote.ReviewRemoteDataSource
import com.moim.core.data.datasource.review.remote.ReviewRemoteDataSourceImpl
import com.moim.core.data.datasource.token.TokenRepository
import com.moim.core.data.datasource.token.TokenRepositoryImpl
import com.moim.core.data.datasource.token.remote.TokenRemoteDataSource
import com.moim.core.data.datasource.token.remote.TokenRemoteDataSourceImpl
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

    //================================ Token ============================================//
    @Singleton
    @Binds
    abstract fun bindTokenRepository(tokenRepositoryImpl: TokenRepositoryImpl): TokenRepository

    @Singleton
    @Binds
    abstract fun bindTokenRemoteDataSource(tokenRemoteDataSourceImpl: TokenRemoteDataSourceImpl): TokenRemoteDataSource

    //================================ User ============================================//
    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindUserRemoteDataSource(userRemoteDataSourceImpl: UserRemoteDataSourceImpl): UserRemoteDataSource

    //================================ Meeting ============================================//
    @Singleton
    @Binds
    abstract fun bindMeetingRepository(meetingRepositoryImpl: MeetingRepositoryImpl): MeetingRepository

    @Singleton
    @Binds
    abstract fun bindMeetingRemoteDataSource(meetingRemoteDataSourceImpl: MeetingRemoteDataSourceImpl): MeetingRemoteDataSource

    //================================ Plan ============================================//
    @Singleton
    @Binds
    abstract fun bindPlanRepository(planRepositoryImpl: PlanRepositoryImpl): PlanRepository

    @Singleton
    @Binds
    abstract fun bindPlanRemoteDataSource(planRemoteDataSourceImpl: PlanRemoteDataSourceImpl): PlanRemoteDataSource

    //================================ Review ============================================//
    @Singleton
    @Binds
    abstract fun bindReviewRepository(reviewRepositoryImpl: ReviewRepositoryImpl): ReviewRepository

    @Singleton
    @Binds
    abstract fun bindReviewRemoteDataSource(reviewRemoteDataSourceImpl: ReviewRemoteDataSourceImpl): ReviewRemoteDataSource

    //================================ Image ============================================//
    @Singleton
    @Binds
    abstract fun bindImageUploadRemoteDataSource(imageUploadRemoteDataSource: ImageUploadRemoteDataSourceImpl): ImageUploadRemoteDataSource
}