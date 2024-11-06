package com.moim.core.data.di.qualifiers

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class NormalApiOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class MoimApiOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class MoimTokenApiOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class MoimApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class NormalApi