package com.arslan.statussaver.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import com.arslan.statussaver.R
import com.arslan.statussaver.models.MediaModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

fun Context.isStatusExist(fileName: String): Boolean {

    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File("${downloadDir}/${getString(R.string.app_name)}", fileName)
        return file.exists()
    } else {
        val downloadUri: Uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Downloads._ID)
        val selection = "${MediaStore.Downloads.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)
        val cursor = contentResolver.query(
            downloadUri,
            projection,
            selection,
            selectionArgs,
            null
        )
        return cursor?.use {
            it.count > 0 // Return true if the file exists
        } ?: false // Return false if cursor is null
    }
}


fun getFileExtension(fileName: String): String {
    val lastDotIndex = fileName.lastIndexOf(".")

    if (lastDotIndex >= 0 && lastDotIndex < fileName.length - 1) {
        return fileName.substring(lastDotIndex + 1)
    }
    return ""
}

fun Context.saveStatus(model: MediaModel): Boolean {

    if (isStatusExist(model.fileName)) {
        return true
    }
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        return saveStatusBeforeQ(this, Uri.parse(model.pathUri))
    }

    val extension = getFileExtension(model.fileName)
    val mimeType = "${model.type}/$extension"
    val inputStream = contentResolver.openInputStream(model.pathUri.toUri())

    try {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.DISPLAY_NAME, model.fileName)
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_DOWNLOADS}/${getString(R.string.app_name)}"
            )
        }

        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                inputStream?.use { inputStream ->
                    outputStream.write(inputStream.readBytes())
                }
            }
            return true
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        inputStream?.close()
    }

    return false
}

private fun saveStatusBeforeQ(context: Context, sourceUri: Uri): Boolean {
    try {
        val sourceFile = File(sourceUri.path ?: return false)
        if (!sourceFile.canRead()) {
            Log.e("SaveStatus", "Source file cannot be read.")
            return false
        }
        val appDir = File(
            "${Environment.getExternalStorageDirectory()}/Download/${context.getString(R.string.app_name)}"
        )

        if (!appDir.exists()) {
            if (!appDir.mkdirs()) {
                Log.e("SaveStatus", "Failed to create destination directory.")
                return false
            }
        }

        val destinationFile = File(appDir, sourceFile.name)
        if (!destinationFile.exists()) {
            destinationFile.createNewFile()
        }

        val sourceChannel = FileInputStream(sourceFile).channel
        val destChannel = FileOutputStream(destinationFile).channel

        destChannel.transferFrom(sourceChannel, 0, sourceChannel.size())

        // Close channels
        sourceChannel.close()
        destChannel.close()

        Log.d("SaveStatus", "File saved successfully at: ${destinationFile.absolutePath}")
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("SaveStatus", "Failed to save file: ${e.message}")
        return false
    }
}

fun Context.deleteStatus(model: MediaModel): Boolean {
    try {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            // For Android 10 and below
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/" + getString(R.string.app_name), model.fileName
            )
            if (file.exists()) {
                return file.delete()
            }
        } else {
            // For Android 11 and above, using MediaStore
            val uri = getFileUri(model)
            if (uri != null) {
                return contentResolver.delete(uri, null, null) > 0
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

private fun Context.getFileUri(model: MediaModel): Uri? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(model.fileName)

        contentResolver.query(
            collection,
            arrayOf(MediaStore.MediaColumns._ID),
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                return ContentUris.withAppendedId(collection, id)
            }
        }
    }
    return null
}