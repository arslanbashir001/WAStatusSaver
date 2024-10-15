package com.arslan.statussaver.data

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import com.arslan.statussaver.models.MEDIA_TYPE_IMAGE
import com.arslan.statussaver.models.MEDIA_TYPE_VIDEO
import com.arslan.statussaver.models.MediaModel
import com.arslan.statussaver.utils.Constants
import com.arslan.statussaver.utils.SharedPrefKeys
import com.arslan.statussaver.utils.SharedPrefUtils
import com.arslan.statussaver.utils.getFileExtension
import com.arslan.statussaver.utils.isStatusExist
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
            val downloadedFolder =
                File(
                    Environment.getExternalStorageDirectory().absolutePath,
                    "Download/Status Saver"
                )

            // Clear the existing list to avoid duplicates
            downloadedStatusesList.clear()

            if (downloadedFolder.exists() && downloadedFolder.isDirectory) {
                downloadedFolder.listFiles()?.forEach { file ->
                    if (file.isFile && file.name != ".nomedia") {
                        val isDownloaded = true // As this is the downloaded folder
                        val type = if (getFileExtension(file.name) == "mp4") {
                            MEDIA_TYPE_VIDEO
                        } else {
                            MEDIA_TYPE_IMAGE
                        }

                        val model = MediaModel(
                            pathUri = file.absolutePath, // Store the Uri as a string
                            fileName = file.name,
                            type = type,
                        )
                        downloadedStatusesList.add(model)
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
        val treeUri = when (whatsAppType) {
            Constants.TYPE_WHATSAPP_MAIN -> {
                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_TREE_URI, "")?.toUri()!!
            }

            else -> {
                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_WP_BUSINESS_TREE_URI, "")
                    ?.toUri()!!
            }
        }

        Log.d(TAG, "getAllStatuses: $treeUri")
        val isAndroid10OrBelow = Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q
        if (isAndroid10OrBelow) {
            // Android 10 and below: Use File API to access the WhatsApp folder directly
            val statusFolder = when (whatsAppType) {
                Constants.TYPE_WHATSAPP_MAIN -> File(Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/.Statuses")
                else -> File(Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp Business/Media/.Statuses")
            }

            statusFolder.listFiles()?.forEach { file ->
                if (file.name != ".nomedia" && file.isFile) {
                    val type =
                        if (getFileExtension(file.name) == "mp4") MEDIA_TYPE_VIDEO else MEDIA_TYPE_IMAGE

                    val model = MediaModel(
                        pathUri = file.absolutePath,  // Using absolute path for older versions
                        fileName = file.name, type = type,
                    )

                    when (whatsAppType) {
                        Constants.TYPE_WHATSAPP_MAIN -> wpStatusesList.add(model)
                        else -> wpBusinessStatusesList.add(model)
                    }
                }
            }
        } else {
            // Android 11 and above: Use DocumentFile API with the treeUri from SAF
            val fileDocument = DocumentFile.fromTreeUri(activity, treeUri)
            fileDocument?.let {
                it.listFiles().forEach { file ->
                    Log.d(TAG, "getAllStatuses: ${file.name}")
                    if (file.name != ".nomedia" && file.isFile) {
                        val isDownloaded = context.isStatusExist(file.name!!)
                        val type =
                            if (getFileExtension(file.name!!) == "mp4") MEDIA_TYPE_VIDEO else MEDIA_TYPE_IMAGE

                        val model = MediaModel(
                            pathUri = file.uri.toString(),
                            fileName = file.name!!,
                            type = type,
                        )
                        when (whatsAppType) {
                            Constants.TYPE_WHATSAPP_MAIN -> wpStatusesList.add(model)
                            else -> wpBusinessStatusesList.add(model)
                        }
                    }
                }
            }
        }


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