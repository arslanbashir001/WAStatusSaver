package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ShareCompat
import androidx.core.content.res.ResourcesCompat
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.ActivityMenuBinding


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
        val uri = Uri.parse("https://sites.google.com/view/statussaver-downloadimagevideo")
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun shareApp() {
        ShareCompat.IntentBuilder.from(this)
            .setType("text/plain")
            .setChooserTitle("Chooser title")
            .setText("Check out this amazing WA Status Saver app! http://play.google.com/store/apps/details?id=$packageName")
            .startChooser();
    }

    private fun rateUs() {
        val uri = Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}