package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.ActivityLanguageSelectionBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.Language
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefKeys
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefUtils
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.getLanguageList
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.adapters.LanguageAdapter
import java.util.Locale

class LanguageSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageSelectionBinding
    private lateinit var adapter: LanguageAdapter
    private lateinit var currentSelectedLanguage: Language

//    private val languages = listOf(
//        Language("en", "English", R.drawable.flag_english),
//        Language("es", "Spanish", R.drawable.flag_spanish),
//        Language("ur", "Urdu", R.drawable.flag_spanish),
//        Language("hi", "Hindi", R.drawable.flag_spanish),
//        Language("fr", "French", R.drawable.flag_spanish),
//        Language("de", "German", R.drawable.flag_germany),
//        Language("ru", "Russian", R.drawable.flag_russia)
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val languages = getLanguageList()

        val storedLanguageCode = SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_SELECTED_LANGUAGE_CODE, "")

        currentSelectedLanguage = if (storedLanguageCode.isNullOrEmpty()) {
            languages.find { it.code == "en" } ?: languages[0]
        } else {
            languages.find { it.code == storedLanguageCode } ?: languages[0]
        }

        // Set up RecyclerView with LanguageAdapter
        binding.languageRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = LanguageAdapter(languages) { language ->
            currentSelectedLanguage = language
        }
        binding.languageRecyclerView.adapter = adapter

        binding.btnDone.setOnClickListener {
            SharedPrefUtils.putPrefString(SharedPrefKeys.PREF_KEY_SELECTED_LANGUAGE_CODE,
                currentSelectedLanguage.code)
            SharedPrefUtils.putPrefBoolean(SharedPrefKeys.PREF_KEY_IS_APP_OPENED_FIRST_TIME, false)
            Toast.makeText(baseContext, "" + currentSelectedLanguage.code, Toast.LENGTH_SHORT).show()
            setLocaleAndSave(currentSelectedLanguage.code)
            navigateToNextActivity()
        }
    }

    fun setLocaleAndSave(languageCode: String) {
        // Set the locale for the app
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        // Update the configuration
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save the language code to SharedPreferences using SharedPrefUtils
//        SharedPrefUtils.putPrefString(SharedPrefKeys.PREF_KEY_SELECTED_LANGUAGE_CODE, languageCode)
    }

    private fun navigateToNextActivity() {
        val options = ActivityOptionsCompat.makeCustomAnimation(this, 0, 0)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (!checkPermission(this@LanguageSelectionActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                startActivity(
                    Intent(this@LanguageSelectionActivity, PermissionActivity::class.java),
                    options.toBundle()
                )
            } else {
                startActivity(
                    Intent(this@LanguageSelectionActivity, MainActivity::class.java),
                    options.toBundle()
                )
            }
        } else {
            startActivity(
                Intent(this@LanguageSelectionActivity, MainActivity::class.java),
                options.toBundle()
            )
        }
        finish()
    }

    private fun checkPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

}










//package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.activities
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
//import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.ActivityLanguageSelectionBinding
//import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.Language
//import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefKeys
//import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefUtils
//import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.adapters.LanguageAdapter
//import java.util.Locale
//
//class LanguageSelectionActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityLanguageSelectionBinding
//    private lateinit var adapter: LanguageAdapter
//    private lateinit var currentSelectedLanguage: Language
//
//    private val languages = listOf(
//        Language("en", "English", R.drawable.flag_english),
//        Language("es", "Spanish", R.drawable.flag_spanish),
//        Language("ur", "Urdu", R.drawable.flag_spanish),
//        Language("hi", "Hindi", R.drawable.flag_spanish),
//        Language("fr", "French", R.drawable.flag_spanish),
//        Language("de", "German", R.drawable.flag_germany),
//        Language("ru", "Russian", R.drawable.flag_russia)
//    )
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLanguageSelectionBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Initialize currentSelectedLanguage to the default language (English)
//        val storedLanguageCode =
//            SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_SELECTED_LANGUAGE_CODE, "")
//        currentSelectedLanguage = if (storedLanguageCode?.isEmpty() == true) {
//            // Default to English if no language has been selected previously
//            languages.find { it.code == "en" } ?: languages[0]
//            // Default to English or the first language in the list
//        } else {
//            // Use the stored language code
//            languages.find { it.code == storedLanguageCode }
//                ?: languages[0] // Fallback to English if code is invalid
//        }
//
//        // Set up RecyclerView with LanguageAdapter
//        binding.languageRecyclerView.layoutManager = LinearLayoutManager(this)
//        adapter = LanguageAdapter(languages) { language ->
//            currentSelectedLanguage = language
//        }
//
//        binding.languageRecyclerView.adapter = adapter
//
//        binding.btnDone.setOnClickListener {
//            changeLanguage(currentSelectedLanguage.code)
//        }
//    }
//
//    private fun changeLanguage(languageCode: String) {
//        val locale = Locale(languageCode)
//        Locale.setDefault(locale)
//        val config = resources.configuration
//        config.setLocale(locale)
//        resources.updateConfiguration(config, resources.displayMetrics)
//
//        // Store the selected language code in SharedPreferences
//        SharedPrefUtils.putPrefString(SharedPrefKeys.PREF_KEY_SELECTED_LANGUAGE_CODE, languageCode)
//
//        // Restart MainActivity with the updated language
//        startActivity(Intent(this@LanguageSelectionActivity, MainActivity::class.java))
//        finish()
//    }
//}


//package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.activities
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
//import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.ActivityLanguageSelectionBinding
//import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.Language
//import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefKeys
//import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefUtils
//import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.adapters.LanguageAdapter
//import java.util.Locale
//
//class LanguageSelectionActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityLanguageSelectionBinding
//    private lateinit var adapter: LanguageAdapter
//    private lateinit var currentSelectedLanguage : Language
//
//    private val languages = listOf(
//        Language("en", "English", R.drawable.flag_english),
//        Language("es", "Spanish", R.drawable.flag_spanish),
//        Language("ur", "Urdu", R.drawable.flag_spanish),
//        Language("hi", "Hindi", R.drawable.flag_spanish),
//        Language("fr", "French", R.drawable.flag_spanish),
//        Language("de", "German", R.drawable.flag_germany),
//        Language("ru", "Russian", R.drawable.flag_russia)
//    )
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLanguageSelectionBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.languageRecyclerView.layoutManager = LinearLayoutManager(this)
//        adapter = LanguageAdapter(languages) { language ->
//            currentSelectedLanguage = language
//        }
//        binding.languageRecyclerView.adapter = adapter
//
//        binding.btnDone.setOnClickListener{
//            changeLanguage(currentSelectedLanguage.code)
//        }
//    }
//
//    private fun changeLanguage(languageCode: String) {
//        val locale = Locale(languageCode)
//        Locale.setDefault(locale)
//        val config = resources.configuration
//        config.setLocale(locale)
//        resources.updateConfiguration(config, resources.displayMetrics)
//
//        SharedPrefUtils.putPrefString(SharedPrefKeys.PREF_KEY_SELECTED_LANGUAGE_CODE, languageCode)
//        startActivity(Intent(this@LanguageSelectionActivity, MainActivity::class.java))
//        finish()
//    }
//}