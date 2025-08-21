package com.moim.feature.plandetail.di

import com.moim.feature.plandetail.util.PlanDetailCommentViewModelDelegate
import com.moim.feature.plandetail.util.PlanDetailCommentViewModelDelegateImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal abstract class DelegateModule {
    @Singleton
    @Binds
    abstract fun bindPlanDetailCommentViewModelDelegate(planDetailCommentViewModelDelegateImpl: PlanDetailCommentViewModelDelegateImpl): PlanDetailCommentViewModelDelegate
}