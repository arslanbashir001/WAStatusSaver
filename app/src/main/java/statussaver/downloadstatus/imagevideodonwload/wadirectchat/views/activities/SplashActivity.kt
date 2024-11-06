package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.ActivitySplashBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefKeys
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefUtils
import java.util.Locale

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPrefUtils.init(this)
        // Initialize SharedPrefUtils and set Locale
        SharedPrefUtils.init(this)
        SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_SELECTED_LANGUAGE_CODE, "")?.let {
            setLocale(it)
        }

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        SharedPrefUtils.init(this)

//        Toast.makeText(baseContext, "" +
//                SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_SELECTED_LANGUAGE_CODE, ""), Toast.LENGTH_SHORT).show()
//        SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_SELECTED_LANGUAGE_CODE, "")
//            ?.let { setLocale(it) }

        val isDarkMode =
            SharedPrefUtils.getPrefBoolean(SharedPrefKeys.PREF_KEY_IS_DARK_MODE_ON, false)
        if (isDarkMode) {
            Log.d("isDark", "onCreate: true")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            window.statusBarColor = ContextCompat.getColor(this, R.color.green_light)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.green_light)
            binding.root.setBackgroundResource(R.drawable.splash_background_night)
            binding.tvDescription.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.splash_description_text_color
                )
            )
        } else {
            Log.d("isDark", "onCreate: false")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            binding.tvDescription.setTextColor(ContextCompat.getColor(this, R.color.black))
            window.navigationBarColor = ContextCompat.getColor(this, R.color.white)
            binding.root.setBackgroundResource(R.drawable.splash_background_light)
        }

        binding.progressBar.max = 5000

        // Create the CountDownTimer for 5 seconds, updating every 10ms
        countDownTimer = object : CountDownTimer(5000, 10) {

            override fun onTick(millisUntilFinished: Long) {
                // Update the progress bar smoothly
                binding.progressBar.progress = (5000 - millisUntilFinished).toInt()
            }

            override fun onFinish() {
                val isAppOpenedFirstTime =
                    SharedPrefUtils.getPrefBoolean(SharedPrefKeys.PREF_KEY_IS_APP_OPENED_FIRST_TIME, true)

                if (isAppOpenedFirstTime) {
                    SharedPrefUtils.putPrefBoolean(SharedPrefKeys.PREF_KEY_IS_APP_OPENED_FIRST_TIME, false)
                    startActivity(Intent(this@SplashActivity, LanguageSelectionActivity::class.java))
                    finish()
                } else {
                    navigateToNextActivity()
                }
            }
        }

        // Start the countdown timer
        countDownTimer.start()
    }


    fun setLocale(languageCode: String) {
        // Set the locale for the app
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        // Update the configuration
        resources.updateConfiguration(config, resources.displayMetrics)
    }


    private fun navigateToNextActivity() {
        val options = ActivityOptionsCompat.makeCustomAnimation(this, 0, 0)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (!checkPermission(this@SplashActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                startActivity(
                    Intent(this@SplashActivity, PermissionActivity::class.java),
                    options.toBundle()
                )
            } else {
                startActivity(
                    Intent(this@SplashActivity, MainActivity::class.java),
                    options.toBundle()
                )
            }
        } else {
            startActivity(
                Intent(this@SplashActivity, MainActivity::class.java),
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

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }
}