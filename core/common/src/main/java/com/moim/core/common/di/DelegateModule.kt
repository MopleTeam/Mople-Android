package com.moim.core.common.di

import com.moim.core.common.delegate.MeetingViewModelDelegate
import com.moim.core.common.delegate.MeetingViewModelDelegateImpl
import com.moim.core.common.delegate.PlanItemViewModelDelegate
import com.moim.core.common.delegate.PlanItemViewModelDelegateImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal abstract class DelegateModule {

    //================================ MeetingViewModelDelegate ============================================//
    @Singleton
    @Binds
    abstract fun bindMeetingViewModelDelegate(meetingViewModelDelegateImpl: MeetingViewModelDelegateImpl): MeetingViewModelDelegate

    //================================ PlanItemViewModelDelegate ============================================//
    @Singleton
    @Binds
    abstract fun bindPlanItemViewModelDelegate(planItemViewModelDelegateImpl: PlanItemViewModelDelegateImpl): PlanItemViewModelDelegate
}