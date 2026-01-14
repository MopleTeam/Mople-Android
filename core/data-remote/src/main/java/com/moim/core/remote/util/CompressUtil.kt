package com.moim.core.remote.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import java.io.File
import javax.inject.Inject

internal class CompressorUtil @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun compressFile(imageFile: File): File =
        if (imageFile.path.contains("compressor")) {
            imageFile
        } else {
            Compressor.compress(
                context = context,
                imageFile = imageFile,
                compressionPatch = {
                    default()
                },
            )
        }
}
