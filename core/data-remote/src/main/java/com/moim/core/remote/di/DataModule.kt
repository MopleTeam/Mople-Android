package com.moim.core.remote.di

import com.moim.core.remote.datasource.auth.AuthRemoteDataSource
import com.moim.core.remote.datasource.auth.AuthRemoteDataSourceImpl
import com.moim.core.remote.datasource.auth.AuthTokenRemoteDataSource
import com.moim.core.remote.datasource.auth.AuthTokenRemoteDataSourceImpl
import com.moim.core.remote.datasource.comment.CommentRemoteDataSource
import com.moim.core.remote.datasource.comment.CommentRemoteDataSourceImpl
import com.moim.core.remote.datasource.holiday.HolidayRemoteDataSource
import com.moim.core.remote.datasource.holiday.HolidayRemoteDataSourceImpl
import com.moim.core.remote.datasource.image.ImageRemoteDataSource
import com.moim.core.remote.datasource.image.ImageRemoteDataSourceImpl
import com.moim.core.remote.datasource.image.ImageUploadRemoteDataSource
import com.moim.core.remote.datasource.image.ImageUploadRemoteDataSourceImpl
import com.moim.core.remote.datasource.location.LocationRemoteDataSource
import com.moim.core.remote.datasource.location.LocationRemoteDataSourceImpl
import com.moim.core.remote.datasource.meeting.MeetingRemoteDataSource
import com.moim.core.remote.datasource.meeting.MeetingRemoteDataSourceImpl
import com.moim.core.remote.datasource.notice.NoticeRemoteDataSource
import com.moim.core.remote.datasource.notice.NoticeRemoteDataSourceImpl
import com.moim.core.remote.datasource.notification.NotificationRemoteDataSource
import com.moim.core.remote.datasource.notification.NotificationRemoteDataSourceImpl
import com.moim.core.remote.datasource.opengraph.OpenGraphRemoteDataSource
import com.moim.core.remote.datasource.opengraph.OpenGraphRemoteDataSourceImpl
import com.moim.core.remote.datasource.plan.PlanRemoteDataSource
import com.moim.core.remote.datasource.plan.PlanRemoteDataSourceImpl
import com.moim.core.remote.datasource.policy.PolicyRemoteDataSource
import com.moim.core.remote.datasource.policy.PolicyRemoteDataSourceImpl
import com.moim.core.remote.datasource.review.ReviewRemoteDataSource
import com.moim.core.remote.datasource.review.ReviewRemoteDataSourceImpl
import com.moim.core.remote.datasource.token.TokenRemoteDataSource
import com.moim.core.remote.datasource.token.TokenRemoteDataSourceImpl
import com.moim.core.remote.datasource.user.UserRemoteDataSource
import com.moim.core.remote.datasource.user.UserRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal abstract class DataModule {
    // ================================ Auth ============================================//
    @Singleton
    @Binds
    abstract fun bindAuthRemoteDataSource(dataSource: AuthRemoteDataSourceImpl): AuthRemoteDataSource

    @Singleton
    @Binds
    abstract fun bindAuthTokenRemoteDataSource(dataSource: AuthTokenRemoteDataSourceImpl): AuthTokenRemoteDataSource

    // ================================ Policy ============================================//
    @Singleton
    @Binds
    abstract fun bindPolicyRemoteDataSource(dataSource: PolicyRemoteDataSourceImpl): PolicyRemoteDataSource

    // ================================ User ============================================//
    @Singleton
    @Binds
    abstract fun bindUserRemoteDataSource(dataSource: UserRemoteDataSourceImpl): UserRemoteDataSource

    // ================================ Meeting ============================================//
    @Singleton
    @Binds
    abstract fun bindMeetingRemoteDataSource(dataSource: MeetingRemoteDataSourceImpl): MeetingRemoteDataSource

    // ================================ Plan ============================================//
    @Singleton
    @Binds
    abstract fun bindPlanRemoteDataSource(dataSource: PlanRemoteDataSourceImpl): PlanRemoteDataSource

    // ================================ Review ============================================//
    @Singleton
    @Binds
    abstract fun bindReviewRemoteDataSource(dataSource: ReviewRemoteDataSourceImpl): ReviewRemoteDataSource

    // ================================ Comment ============================================//
    @Singleton
    @Binds
    abstract fun bindCommentRemoteDataSource(dataSource: CommentRemoteDataSourceImpl): CommentRemoteDataSource

    // ================================ Notice ============================================//
    @Singleton
    @Binds
    abstract fun bindNoticeRemoteDataSource(dataSource: NoticeRemoteDataSourceImpl): NoticeRemoteDataSource

    // ================================ Notification ============================================//
    @Singleton
    @Binds
    abstract fun bindNotificationRemoteDataSource(dataSource: NotificationRemoteDataSourceImpl): NotificationRemoteDataSource

    // ================================ Token ============================================//
    @Singleton
    @Binds
    abstract fun bindTokenRemoteDataSource(dataSource: TokenRemoteDataSourceImpl): TokenRemoteDataSource

    // ================================ Location ============================================//
    @Singleton
    @Binds
    abstract fun bindLocationRemoteDataSource(dataSource: LocationRemoteDataSourceImpl): LocationRemoteDataSource

    // ================================ Holiday ============================================//
    @Singleton
    @Binds
    abstract fun bindHolidayRemoteDataSource(dataSource: HolidayRemoteDataSourceImpl): HolidayRemoteDataSource

    // ================================ OpenGraph ============================================//
    @Singleton
    @Binds
    abstract fun bindOpenGraphRemoteDataSource(dataSource: OpenGraphRemoteDataSourceImpl): OpenGraphRemoteDataSource

    // ================================ Image ============================================//
    @Singleton
    @Binds
    abstract fun bindImageRemoteDataSource(dataSource: ImageRemoteDataSourceImpl): ImageRemoteDataSource

    @Singleton
    @Binds
    abstract fun bindImageUploadRemoteDataSource(dataSource: ImageUploadRemoteDataSourceImpl): ImageUploadRemoteDataSource
}
