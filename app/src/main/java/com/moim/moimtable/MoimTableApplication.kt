package com.moim.moimtable

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class MoimTableApplication :
    Application(),
    SingletonImageLoader.Factory {
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader
            .Builder(this)
            .components { add(SvgDecoder.Factory()) }
            .crossfade(true)
            .crossfade(500)
            .build()
}
