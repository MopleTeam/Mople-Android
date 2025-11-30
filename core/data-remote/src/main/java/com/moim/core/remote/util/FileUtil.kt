package com.moim.core.remote.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.provider.OpenableColumns
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.core.graphics.createBitmap

internal class FileUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val contentResolver = context.contentResolver

    fun from(path: String): File {
        val uri = path.toUri()

        return if (uri.scheme.isNullOrEmpty()) {
            File(path)
        } else {
            val fileName = getFileName(uri)
            val splitName = splitFileName(fileName)

            val inputStream: InputStream? = try {
                contentResolver.openInputStream(uri)
            } catch (e: FileNotFoundException) {
                dummyFileInputStream()
            }

            val tempFile = rename(File.createTempFile(DEFAULT_PREFIX_FILE.plus(DEFAULT_PREFIX_FILE), splitName[1]), fileName).also { it.deleteOnExit() }

            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(tempFile)
                if (inputStream != null) {
                    copy(inputStream, out)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                inputStream?.close()
                out?.close()
            }

            tempFile
        }
    }

    private fun splitFileName(fileName: String?): Array<String?> {
        var name = fileName
        var extension: String? = ""
        val i = fileName!!.lastIndexOf(".")
        if (i != -1) {
            name = fileName.substring(0, i)
            extension = fileName.substring(i)
        }
        return arrayOf(name, extension)
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }

        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf(File.separator)
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result!!
    }

    private fun rename(file: File, newName: String): File {
        val newFile = File(file.parent, newName)
        if (newFile != file) {
            if (newFile.exists() && newFile.delete()) {
                Timber.d("[rename] Delete old $newName file")
            }
            if (file.renameTo(newFile)) {
                Timber.d("[rename] Rename file to $newName")
            }
        }
        return newFile
    }

    private fun dummyFileInputStream(): InputStream {
        var bos = ByteArrayOutputStream()
        var bitmap: Bitmap? = null

        try {
            bitmap = createBitmap(320, 320)
            Canvas(bitmap).apply { drawColor(Color.LTGRAY) }
            bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bitmap?.recycle()
        }
        return ByteArrayInputStream(bos.toByteArray())
    }

    private fun copy(input: InputStream, output: OutputStream): Long {
        var count: Long = 0
        var n: Int
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (EOF != input.read(buffer).also { n = it }) {
            output.write(buffer, 0, n)
            count += n.toLong()
        }
        return count
    }

    companion object {
        private const val EOF = -1
        private const val DEFAULT_BUFFER_SIZE = 1024 * 4
        private const val DEFAULT_PREFIX_FILE = "IMG_"
    }
}