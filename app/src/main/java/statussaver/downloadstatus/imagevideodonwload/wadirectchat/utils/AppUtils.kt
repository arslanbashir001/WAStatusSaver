package statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.BuildConfig
import java.io.File

fun Context.shareStatus(fileUri: Uri) {
    val shareUri = if (fileUri.scheme == "file") {
        val file = File(fileUri.path ?: "")
        FileProvider.getUriForFile(
            this,
            "${BuildConfig.APPLICATION_ID}.provider", // Replace with your package name
            file
        )
    } else {
        fileUri
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*" // Change to "video/*" if sharing a video
        putExtra(Intent.EXTRA_STREAM, shareUri)
        putExtra(
            Intent.EXTRA_TEXT,
            "Check out this status! You can download my app from: <link_to_your_app>"
        )
    }

    val chooserIntent = Intent.createChooser(shareIntent, "Share via")
    startActivity(chooserIntent)
}

fun Context.repostStatus(fileUri: Uri) {
    // Convert fileUri to a content URI if it's a file URI
    val shareUri = if (fileUri.scheme == "file") {
        // Convert file URI to content URI using FileProvider
        val file = File(fileUri.path ?: "")
        FileProvider.getUriForFile(
            this,
            "${BuildConfig.APPLICATION_ID}.provider", // Replace with your package name
            file
        )
    } else {
        fileUri // Keep the content URI as is
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*" // Change to "video/*" if sharing a video
        putExtra(Intent.EXTRA_STREAM, shareUri)
    }

    val appList = mutableListOf<Intent>()
    val whatsappIntent = Intent(shareIntent).apply {
        setPackage("com.whatsapp")
    }
    if (isAppInstalled("com.whatsapp")) {
        appList.add(whatsappIntent)
    }

    val whatsappBusinessIntent = Intent(shareIntent).apply {
        setPackage("com.whatsapp.w4b")
    }
    if (isAppInstalled("com.whatsapp.w4b")) {
        appList.add(whatsappBusinessIntent)
    }

    if (appList.isNotEmpty()) {
        val chooserIntent = Intent.createChooser(appList.removeAt(0), "Share via").apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, appList.toTypedArray())
        }
        startActivity(chooserIntent)
    } else {
        Toast.makeText(this, "No WhatsApp Installed", Toast.LENGTH_SHORT).show()
    }
}

fun Context.isAppInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun Context.installWhatsApp(packageName: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(intent)
    } catch (e: Exception) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(intent)
    }
}



//// Extension function to load countries from a JSON file
//fun Context.loadCountriesFromJson(fileName: String): ArrayList<Country> {
//    val jsonString: String
//    val countries: ArrayList<Country>
//
//    // Load the JSON file from assets
//    this.assets.open(fileName).use { inputStream ->
//        BufferedReader(InputStreamReader(inputStream)).use { reader ->
//            jsonString = reader.readText()
//        }
//    }
//
//    // Parse the JSON string into an ArrayList
//    val gson = Gson()
//    val listType = object : TypeToken<ArrayList<Country>>() {}.type
//    countries = gson.fromJson(jsonString, listType)
//
//    return countries
//}
//
//fun List<Country>.getCountryByCode(code: String): Country? {
//    return this.find { it.isoCode.equals(code, ignoreCase = true) }
//}

