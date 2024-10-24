package statussaver.downloadstatus.imagevideodonwload.wadirectchat.data

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.MEDIA_TYPE_IMAGE
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.MEDIA_TYPE_VIDEO
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.MediaModel
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.Constants
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefKeys
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefUtils
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.getFileExtension
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.isStatusExist
import java.io.File

class StatusRepo(val context: Context) {

    val whatsAppStatusesLiveData = MutableLiveData<ArrayList<MediaModel>>()
    val whatsAppBusinessStatusesLiveData = MutableLiveData<ArrayList<MediaModel>>()
    val downloadedStatusesLiveData = MutableLiveData<ArrayList<MediaModel>>()

    val activity = context as Activity

    private val wpStatusesList = ArrayList<MediaModel>()
    private val wpBusinessStatusesList = ArrayList<MediaModel>()
    private val downloadedStatusesList = ArrayList<MediaModel>()

    private val TAG = "StatusRepo"

    fun getDownloadedStatuses() {
        Thread {
            downloadedStatusesList.clear()

            // Check if the device is running Android 10 (API level 29) or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore API to access files for Android 10+
                val projection = arrayOf(
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DISPLAY_NAME,
                    MediaStore.Files.FileColumns.MIME_TYPE,
                    MediaStore.Files.FileColumns.RELATIVE_PATH
                )

                val selection = MediaStore.Files.FileColumns.RELATIVE_PATH + " LIKE ? "
                val selectionArgs = arrayOf("%Download/Status Saver%")

                val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)

                val cursor = context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )

                cursor?.use {
                    val idColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                    val nameColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)

                    while (it.moveToNext()) {
                        val id = it.getLong(idColumn)
                        val fileName = it.getString(nameColumn)
                        val mimeType = it.getString(mimeTypeColumn)

                        val type = if (mimeType == "video/mp4") MEDIA_TYPE_VIDEO else MEDIA_TYPE_IMAGE
                        val contentUri = ContentUris.withAppendedId(collection, id)

                        val model = MediaModel(
                            pathUri = contentUri.toString(),
                            fileName = fileName,
                            type = type
                        )
                        downloadedStatusesList.add(model)
                    }
                }
            } else {
                // Use the traditional file-based method for Android 9 and below
                val downloadedFolder = File(
                    Environment.getExternalStorageDirectory().absolutePath,
                    "Download/Status Saver"
                )

                if (downloadedFolder.exists() && downloadedFolder.isDirectory) {
                    downloadedFolder.listFiles()?.forEach { file ->
                        if (file.isFile && file.name != ".nomedia") {
                            val type = if (getFileExtension(file.name) == "mp4") {
                                MEDIA_TYPE_VIDEO
                            } else {
                                MEDIA_TYPE_IMAGE
                            }

                            val model = MediaModel(
                                pathUri = file.absolutePath,
                                fileName = file.name,
                                type = type,
                            )
                            downloadedStatusesList.add(model)
                        }
                    }
                }
            }

            Log.d("getDown", "getDownloadedStatuses: ${downloadedStatusesList.size}")

            // Post the updated list to LiveData on the main thread
            Handler(Looper.getMainLooper()).post {
                downloadedStatusesLiveData.postValue(downloadedStatusesList)
            }
        }.start() // Start the thread
    }


    fun getAllStatuses(whatsAppType: String = Constants.TYPE_WHATSAPP_MAIN) {
        // Launch a coroutine to run the process in a background thread (IO Dispatcher for I/O operations)
        CoroutineScope(Dispatchers.IO).launch {
            val treeUri = when (whatsAppType) {
                Constants.TYPE_WHATSAPP_MAIN -> {
                    SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, "")?.toUri()!!
                }

                else -> {
                    SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_BUSINESS_TREE_URI, "")
                        ?.toUri()!!
                }
            }

            Log.d(TAG, "getAllStatuses tree uri: $treeUri")
            val isAndroid10OrBelow = Build.VERSION.SDK_INT <= Build.VERSION_CODES.P

            if (isAndroid10OrBelow) {
                Log.d(TAG, "Android 10 and below")
                // Android 10 and below: Use File API to access the WhatsApp folder directly
                val statusFolder = when (whatsAppType) {
                    Constants.TYPE_WHATSAPP_MAIN -> File(Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/.Statuses")
                    else -> File(Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp Business/Media/.Statuses")
                }

                statusFolder.listFiles()?.forEach { file ->
                    if (file.name != ".nomedia" && file.isFile) {
                        val type = if (getFileExtension(file.name) == "mp4") MEDIA_TYPE_VIDEO else MEDIA_TYPE_IMAGE

                        val model = MediaModel(
                            pathUri = file.absolutePath,  // Using absolute path for older versions
                            fileName = file.name,
                            type = type
                        )

                        when (whatsAppType) {
                            Constants.TYPE_WHATSAPP_MAIN -> wpStatusesList.add(model)
                            else -> wpBusinessStatusesList.add(model)
                        }
                    }
                }

            } else {
                Log.d(TAG, "Android 11 and above")
                val fileDocument = DocumentFile.fromTreeUri(activity, treeUri)
                if (fileDocument?.isDirectory == true) {
                    Log.d(TAG, "getAllStatuses size: " + fileDocument.listFiles().size)
                    fileDocument.listFiles().forEach { file ->
                        Log.d(TAG, "getAllStatuses filename: ${file.name}")
                        try {
                            if (file.name != ".nomedia" && file.isFile) {
                                val type = if (getFileExtension(file.name!!) == "mp4") MEDIA_TYPE_VIDEO else MEDIA_TYPE_IMAGE

                                val model = MediaModel(
                                    pathUri = file.uri.toString(),
                                    fileName = file.name!!,
                                    type = type
                                )

                                when (whatsAppType) {
                                    Constants.TYPE_WHATSAPP_MAIN -> wpStatusesList.add(model)
                                    else -> wpBusinessStatusesList.add(model)
                                }
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "Exception ${file.name}: " + e)
                        }
                    }
                } else {
                    Log.d(TAG, "fileDocument is not a directory or is null.")
                }
            }

            // Posting the value to LiveData after the background processing is completed
            when (whatsAppType) {
                Constants.TYPE_WHATSAPP_MAIN -> {
                    Log.d(TAG, "getAllStatuses: Pushing Value to Wp live Data")
                    whatsAppStatusesLiveData.postValue(wpStatusesList)
                }
                else -> {
                    Log.d(TAG, "getAllStatuses: Pushing Value to Wp Business live Data")
                    whatsAppBusinessStatusesLiveData.postValue(wpBusinessStatusesList)
                }
            }
        }
    }


//    fun getAllStatuses(whatsAppType: String = Constants.TYPE_WHATSAPP_MAIN) {
//        val treeUri = when (whatsAppType) {
//            Constants.TYPE_WHATSAPP_MAIN -> {
//                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, "")?.toUri()!!
//            }
//
//            else -> {
//                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_BUSINESS_TREE_URI, "")
//                    ?.toUri()!!
//            }
//        }
//
//        Log.d(TAG, "getAllStatuses tree uri: $treeUri")
//        val isAndroid10OrBelow = Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
//        if (isAndroid10OrBelow) {
//            Log.d(TAG, "Android 10 and below")
//            // Android 10 and below: Use File API to access the WhatsApp folder directly
//            val statusFolder = when (whatsAppType) {
//                Constants.TYPE_WHATSAPP_MAIN -> File(Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/.Statuses")
//                else -> File(Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp Business/Media/.Statuses")
//            }
//
//            statusFolder.listFiles()?.forEach { file ->
//                if (file.name != ".nomedia" && file.isFile) {
//                    val type =
//                        if (getFileExtension(file.name) == "mp4") MEDIA_TYPE_VIDEO else MEDIA_TYPE_IMAGE
//
//                    val model = MediaModel(
//                        pathUri = file.absolutePath,  // Using absolute path for older versions
//                        fileName = file.name, type = type,
//                    )
//
//                    when (whatsAppType) {
//                        Constants.TYPE_WHATSAPP_MAIN -> wpStatusesList.add(model)
//                        else -> wpBusinessStatusesList.add(model)
//                    }
//                }
//            }
//        }
//
//
//        else {
//            Log.d(TAG, "Android 11 and above")
//            val fileDocument = DocumentFile.fromTreeUri(activity, treeUri)
//            if (fileDocument?.isDirectory == true) {
//                Log.d(TAG, "getAllStatuses size: " + fileDocument.listFiles().size)
//                    fileDocument.let {
//                        it.listFiles().forEach { file ->
//                            Log.d(TAG, "getAllStatuses filename: ${file.name}")
//                            try {
//                                if (file.name != ".nomedia" && file.isFile) {
//
//                                    val type =
//                                        if (getFileExtension(file.name!!) == "mp4")
//                                            MEDIA_TYPE_VIDEO else MEDIA_TYPE_IMAGE
//
//                                    val model = MediaModel(
//                                        pathUri = file.uri.toString(),
//                                        fileName = file.name!!,
//                                        type = type,
//                                    )
//                                    when (whatsAppType) {
//                                        Constants.TYPE_WHATSAPP_MAIN -> wpStatusesList.add(model)
//                                        else -> wpBusinessStatusesList.add(model)
//                                    }
//                                }
//                            }catch (e:Exception){
//                                Log.d(TAG, "Exception ${file.name}: " + e )
//                            }
//                        }
//                    }
//
//            } else {
//                Log.d(TAG, "fileDocument is not a directory or is null.")
//            }
//        }
//
//
//        when (whatsAppType) {
//            Constants.TYPE_WHATSAPP_MAIN -> {
//                Log.d(TAG, "getAllStatuses: Pushing Value to Wp live Data")
//                whatsAppStatusesLiveData.postValue(wpStatusesList)
//            }
//
//            else -> {
//                Log.d(TAG, "getAllStatuses: Pushing Value to Wp Business live Data")
//                whatsAppBusinessStatusesLiveData.postValue(wpBusinessStatusesList)
//            }
//        }
//    }
}