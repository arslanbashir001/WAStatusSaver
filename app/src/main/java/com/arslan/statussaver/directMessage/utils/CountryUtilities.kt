package com.arslan.statussaver.directMessage.utils

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.annotation.StringRes
import com.arslan.statussaver.R
import com.arslan.statussaver.directMessage.model.Country
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

////try to match the device's country code with a country code in our list.
//private fun getDefaultCountry(): Country? {
//
//    val deviceCountryCode = Locale.getDefault().country
//
//    //see if any iso code matches device iso code
//    for (country in countries) {
//        if (country.isoCode.lowercase() == deviceCountryCode.lowercase()) return country
//    }
//
//    return null
//}
//
////this is used repeatedly. We might as well create a field for it.
//val deviceDefaultCountry = getDefaultCountry()


//extension function on Context to create a toast.
fun Context.createToast(@StringRes messageResource: Int) {
    Toast.makeText(this, messageResource, Toast.LENGTH_SHORT).show()
}


//detect a country using the ISD code in the beginning.
fun detectCountry( context: Context, phoneNumber: String): Country? {
    context.loadCountriesFromJson("country_data.json").forEach {
        if (phoneNumber.replace("+", "").startsWith(it.isdCode)) return it
    }
    return null
}



fun Context.getDrawableFromAssets(imageName: String): Drawable? {
    return try {
        val inputStream: InputStream = assets.open("flags/$imageName.png")
        Drawable.createFromStream(inputStream, null)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getLaunchIntent(phoneNumber: String, message: String, business: Boolean): Intent {

    val total = "https://api.whatsapp.com/send?phone=" +
            phoneNumber.replace("+", "") + "&text=${message}"

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(total)
        `package` = if (business) "com.whatsapp.w4b" else "com.whatsapp"
    }
    return intent
}



fun getLaunchIntentForShareLink(phoneNumber: String, message: String): Intent {
    val total = "https://api.whatsapp.com/send?phone=" +
            phoneNumber.replace("+", "") + "&text=${message}"

    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, total)
        type = "text/plain"
    }

    return Intent.createChooser(intent, "Share via")
}


fun Intent.launchIfResolved(context: Context) {
    if (resolveActivity(context.packageManager) == null) context.createToast(R.string.not_installed)
    else context.startActivity(this)
}

fun Context.loadCountriesFromJson(fileName: String): ArrayList<Country> {
    val jsonString: String
    val countries: ArrayList<Country>

    // Load the JSON file from assets
    this.assets.open(fileName).use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            jsonString = reader.readText()
        }
    }

    // Parse the JSON string into an ArrayList
    val gson = Gson()
    val listType = object : TypeToken<ArrayList<Country>>() {}.type
    countries = gson.fromJson(jsonString, listType)

    return countries
}

fun List<Country>.getCountryByCode(code: String): Country? {
    return this.find { it.isoCode.equals(code, ignoreCase = true) }
}
