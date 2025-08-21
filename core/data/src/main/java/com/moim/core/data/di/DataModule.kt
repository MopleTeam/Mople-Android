package com.moim.core.data.di

import com.moim.core.data.datasource.auth.AuthRepository
import com.moim.core.data.datasource.auth.AuthRepositoryImpl
import com.moim.core.data.datasource.comment.CommentRepository
import com.moim.core.data.datasource.comment.CommentRepositoryImpl
import com.moim.core.data.datasource.holiday.HolidayRepository
import com.moim.core.data.datasource.holiday.HolidayRepositoryImpl
import com.moim.core.data.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.data.datasource.image.ImageUploadRemoteDataSourceImpl
import com.moim.core.data.datasource.meeting.MeetingRepository
import com.moim.core.data.datasource.meeting.MeetingRepositoryImpl
import com.moim.core.data.datasource.notification.NotificationRepository
import com.moim.core.data.datasource.notification.NotificationRepositoryImpl
import com.moim.core.data.datasource.plan.PlanRepository
import com.moim.core.data.datasource.plan.PlanRepositoryImpl
import com.moim.core.data.datasource.policy.PolicyRepository
import com.moim.core.data.datasource.policy.PolicyRepositoryImpl
import com.moim.core.data.datasource.review.ReviewRepository
import com.moim.core.data.datasource.review.ReviewRepositoryImpl
import com.moim.core.data.datasource.token.TokenRepository
import com.moim.core.data.datasource.token.TokenRepositoryImpl
import com.moim.core.data.datasource.user.UserRepository
import com.moim.core.data.datasource.user.UserRepositoryImpl
import com.moim.core.data.util.UserDataUtilImpl
import com.moim.core.network.util.UserDataUtil
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

    //================================ Policy ============================================//
    @Singleton
    @Binds
    abstract fun bindPolicyRepository(policyRepositoryImpl: PolicyRepositoryImpl): PolicyRepository

    //================================ Token ============================================//
    @Singleton
    @Binds
    abstract fun bindTokenRepository(tokenRepositoryImpl: TokenRepositoryImpl): TokenRepository

    //================================ User ============================================//
    @Singleton
    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    //================================ Meeting ============================================//
    @Singleton
    @Binds
    abstract fun bindMeetingRepository(meetingRepositoryImpl: MeetingRepositoryImpl): MeetingRepository

    //================================ Plan ============================================//
    @Singleton
    @Binds
    abstract fun bindPlanRepository(planRepositoryImpl: PlanRepositoryImpl): PlanRepository

    //================================ Review ============================================//
    @Singleton
    @Binds
    abstract fun bindReviewRepository(reviewRepositoryImpl: ReviewRepositoryImpl): ReviewRepository

    //================================ Comment ============================================//
    @Singleton
    @Binds
    abstract fun bindCommentRepository(commentRepositoryImpl: CommentRepositoryImpl): CommentRepository

    //================================ Notification ============================================//
    @Singleton
    @Binds
    abstract fun bindNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository

    //================================ Holiday ============================================//
    @Singleton
    @Binds
    abstract fun bindHolidayRepository(holidayRepositoryImpl: HolidayRepositoryImpl) : HolidayRepository

    //================================ Image ============================================//
    @Singleton
    @Binds
    abstract fun bindImageUploadRemoteDataSource(imageUploadRemoteDataSource: ImageUploadRemoteDataSourceImpl): ImageUploadRemoteDataSource

    //================================ UserDataUtil ============================================//
    @Singleton
    @Binds
    abstract fun bindUserDataUtil(userDataUtilImpl: UserDataUtilImpl): UserDataUtil
}