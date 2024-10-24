package statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils

import android.net.Uri
import android.os.Build

object Constants {

    const val TYPE_WHATSAPP_MAIN = "com.whatsapp"
    const val TYPE_WHATSAPP_BUSINESS = "com.whatsapp.w4b"

    const val MEDIA_TYPE_WHATSAPP_IMAGES = "com.whatsapp.images"
    const val MEDIA_TYPE_WHATSAPP_VIDEOS = "com.whatsapp.videos"

    const val MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES = "com.whatsapp.w4b.images"
    const val MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS = "com.whatsapp.w4b.videos"


    const val MEDIA_LIST_KEY = "MEDIA_LIST"
    const val MEDIA_SCROLL_KEY = "MEDIA_SCROLL"
    const val MEDIA_TYPE_KEY = "MEDIA_TYPE"

    const val SWITCH_BUTTON_KEY = "switch"
    const val SWITCH_BUTTON_PREF_KEY = "switchPref"

//    const val SELECTED_COUNTRY_KEY = "selectedCountry"
    const val SELECTED_COUNTRY_PREF_KEY = "selectedCountryPref"
    const val SELECTED_COUNTRY_DEVICE_DEFAULT_PREF_KEY = "selectedCountryDeviceDefaultPref"


    const val IS_DOWNLOADED_STATUSES = "isDownloadedStatuses"
    const val MEDIA_MODEL = "mediaModel"

    // URIs
    val WHATSAPP_PATH_URI_ANDROID =
        Uri.parse("content://com.android.externalstorage.documents/document/primary%3AWhatsApp%2FMedia%2F.Statuses")
    val WHATSAPP_PATH_URI_ANDROID_11 =
        Uri.parse("content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses")
    val WHATSAPP_BUSINESS_PATH_URI_ANDROID =
        Uri.parse("content://com.android.externalstorage.documents/document/primary%3AWhatsApp%20Business%2FMedia%2F.Statuses")
    val WHATSAPP_BUSINESS_PATH_URI_ANDROID_11 =
        Uri.parse("content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp%20Business%2FMedia%2F.Statuses")

    fun getWhatsappUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            WHATSAPP_PATH_URI_ANDROID_11
        } else {
            WHATSAPP_PATH_URI_ANDROID
        }
    }
    fun getWhatsappBusinessUri(): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            WHATSAPP_BUSINESS_PATH_URI_ANDROID_11
        } else {
            WHATSAPP_BUSINESS_PATH_URI_ANDROID
        }
    }

    // New media type constants
    const val MEDIA_TYPE_DOWNLOADED_IMAGES = "com.downloader.images"
    const val MEDIA_TYPE_DOWNLOADED_VIDEOS = "com.downloader.videos"
}