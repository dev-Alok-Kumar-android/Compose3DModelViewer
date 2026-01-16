package com.tuto.alokkumar.tuto3d.data.model

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ModelFileRepository(private val context: Context) {

    suspend fun copyUriToCache(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            // Create a unique file name
            val originalName = context.getFileNameFromUri(uri)
            val ext = originalName?.substringAfterLast('.', "")?.takeIf { it.isNotBlank() } ?: "glb"
            val fileName = "imported_model_${System.currentTimeMillis()}.$ext"
            val file = File(context.cacheDir, fileName)

            val inputStream = context.contentResolver.openInputStream(uri)

            if (inputStream != null) {
                val outputStream = FileOutputStream(file)
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                return@withContext file.absolutePath
            } else {
                return@withContext null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }


    fun clearCache() {
        try {
            context.cacheDir.listFiles()?.forEach {
                if (it.name.startsWith("imported_model_")) it.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun Context.getFileNameFromUri(uri: Uri): String? {
    return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex == -1) return null

        if (!cursor.moveToFirst()) return null
        cursor.getString(nameIndex)
    }
}
