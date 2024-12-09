package com.moim.core.common.di

import com.moim.core.common.delegate.MeetingViewModelDelegate
import com.moim.core.common.delegate.MeetingViewModelDelegateImpl
import com.moim.core.common.delegate.PlanViewModelDelegate
import com.moim.core.common.delegate.PlanViewModelDelegateImpl
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

    //================================ PlanViewModelDelegate ============================================//
    @Singleton
    @Binds
    abstract fun bindPlanViewModelDelegate(planViewModelDelegateImpl: PlanViewModelDelegateImpl): PlanViewModelDelegate
}