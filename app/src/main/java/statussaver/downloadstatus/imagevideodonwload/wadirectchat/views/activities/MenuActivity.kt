package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ShareCompat
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.customSwtich.IconSwitch
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.customSwtich.IconSwitch.Checked
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.ActivityMenuBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefKeys
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefUtils
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.getLanguageList
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.getLanguageNameByCode


class MenuActivity : AppCompatActivity(), IconSwitch.CheckedChangeListener {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.iconSwitch.setCheckedChangeListener(this)

        // Check current theme and set switch accordingly
        val isDarkMode = SharedPrefUtils.getPrefBoolean(SharedPrefKeys.PREF_KEY_IS_DARK_MODE_ON, false)
        Log.d("isDark", "onCreate: $isDarkMode")

        if (isDarkMode) {
            binding.iconSwitch.checked = Checked.RIGHT
        } else {
            binding.iconSwitch.checked = Checked.LEFT
        }
        updateTheme(isDarkMode)

        clickListeners()
    }

    private fun updateTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


    private fun clickListeners() {


        binding.tvLanguageSelectionMenu.text =
            SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_SELECTED_LANGUAGE_CODE, "en")
                ?.let {
                    getLanguageList().getLanguageNameByCode(
                        it
                    )
                }


        binding.layoutSelectLanguage.setOnClickListener {
            // Handle language selection
            startActivity(Intent(this@MenuActivity, LanguageSelectionActivity::class.java))
        }

        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            // Handle notification switch change
        }

//        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
//            SharedPrefUtils.putPrefBoolean(SharedPrefKeys.PREF_KEY_IS_DARK_MODE_ON, isChecked)
//                updateTheme(isChecked)
//        }

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

    override fun onCheckChanged(current: Checked?) {
        if (current == Checked.LEFT) {
            SharedPrefUtils.putPrefBoolean(SharedPrefKeys.PREF_KEY_IS_DARK_MODE_ON, false)
            Handler(Looper.getMainLooper()).postDelayed({
                updateTheme(false)
            }, 300)

        } else {
            SharedPrefUtils.putPrefBoolean(SharedPrefKeys.PREF_KEY_IS_DARK_MODE_ON, true)
            Handler(Looper.getMainLooper()).postDelayed({
                updateTheme(true)
            }, 300)
        }

    }
}