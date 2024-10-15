package com.arslan.statussaver.views.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.arslan.statussaver.R
import com.arslan.statussaver.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())
    private var progressStatus = 0
    private val totalDuration = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.background_color, null)
        window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.background_color, null)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR


        // Initialize the ProgressBar
        binding.progressBar.max = 100

        // Start the smooth progress update
        Thread {
            while (progressStatus < 100) {
                progressStatus += 1
                // Update the progress on the UI thread
                handler.post { binding.progressBar.progress = progressStatus }
                try {
                    // Sleep for a short duration to create a smooth effect
                    Thread.sleep((totalDuration / 100).toLong()) // Update every 130 milliseconds
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }

            // Check the Android version
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                if (!checkPermission(this@SplashActivity, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    !checkPermission(this@SplashActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    // Navigate to PermissionActivity if version is <= Q
                    startActivity(Intent(this@SplashActivity, PermissionActivity::class.java))
                }else {
                    // Navigate to MainActivity otherwise
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            finish()
        }.start()
    }

    // Check if a specific permission is granted
    private fun checkPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
