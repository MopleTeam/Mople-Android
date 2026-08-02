package com.moim.core.remote.di.qualifiers

import javax.inject.Qualifier

// 토큰 불필요
@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class NormalApi

// 토큰 필요
@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class MoimApi

// 토큰 갱신 전용 (갱신 로직 재귀 방지)
@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class TokenRefreshApi
