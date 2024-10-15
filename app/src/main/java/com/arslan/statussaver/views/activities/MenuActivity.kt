package com.arslan.statussaver.views.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import com.arslan.statussaver.R
import com.arslan.statussaver.databinding.ActivityMenuBinding


class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.background_color, null)
        window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.background_color, null)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        clickListeners()
    }



    private fun clickListeners() {
        binding.layoutSelectLanguage.setOnClickListener {
            // Handle language selection
        }

        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            // Handle notification switch change
        }

        binding.layoutPrivacyPolicy.setOnClickListener {
            openPrivacyPolicy()
        }

        binding.layoutShareApp.setOnClickListener {
            shareApp()
        }

        binding.layoutRateUs.setOnClickListener {
            rateUs()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun openPrivacyPolicy() {
        // Implement your logic to open the privacy policy
    }

    private fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Check out this amazing WP Status Saver app!")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun rateUs() {
        val uri = Uri.parse("market://details?id=${packageName}")
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}
