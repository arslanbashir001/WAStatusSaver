package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.activities


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.customSwtich.IconSwitch.Checked
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.ActivityMainBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.Constants
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefKeys
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefUtils
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.slideToEndWithFadeOut
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.adapters.ViewPagerAdapter

class MainActivity : AppCompatActivity(),
    statussaver.downloadstatus.imagevideodonwload.wadirectchat.customSwtich.IconSwitch.CheckedChangeListener {
    private lateinit var binding: ActivityMainBinding
    private var isDirectChatFragment: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.iconSwitch.setCheckedChangeListener(this)
        SharedPrefUtils.init(this)


        val viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.isUserInputEnabled = false

        val isBusinessWhatsApp = SharedPrefUtils.getPrefBoolean(Constants.SWITCH_BUTTON_KEY, false)
        if (isBusinessWhatsApp) {
            binding.iconSwitch.checked = Checked.RIGHT
            updateUI(true)
        } else {
            binding.iconSwitch.checked = Checked.LEFT
            updateUI(false)
        }


        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_status -> {
                    binding.toolBarTitle.text = "WA Status Saver"
                    isDirectChatFragment = false
                    if (SharedPrefUtils.getPrefBoolean(Constants.SWITCH_BUTTON_KEY, false)) {
                        updateUI(true)
                    } else {
                        updateUI(false)
                    }
                    true
                }

                R.id.menu_downloads -> {
                    binding.toolBarTitle.text = "Downloaded"
                    binding.viewPager.setCurrentItem(2, false)
                    binding.iconSwitch.visibility = View.GONE
                    true
                }

                R.id.menu_direct_chat -> {
                    binding.toolBarTitle.text = "Direct Chat"
                    isDirectChatFragment = true
                    binding.viewPager.setCurrentItem(3, false)
                    binding.iconSwitch.visibility = View.VISIBLE
                    true
                }

                else -> false
            }
        }

        clickListeners()
    }

    private fun clickListeners() {
        binding.btnMenu.setOnClickListener {
            val intent = Intent(this@MainActivity, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUI(isBusinessWhatsApp: Boolean) {
        if (isBusinessWhatsApp) {
            binding.viewPager.setCurrentItem(1, false)
            binding.iconSwitch.visibility = View.VISIBLE
        } else {
            binding.viewPager.setCurrentItem(0, false)
            binding.iconSwitch.visibility = View.VISIBLE
        }
    }


    private var isUpdatingUI = false

    override fun onCheckChanged(current: Checked?) {
        if (isUpdatingUI) return // Prevent multiple calls

        isUpdatingUI = true
        Log.d("checked", "onCheckChanged: $current")
        if (current == Checked.LEFT) {
            SharedPrefUtils.putPrefBoolean(Constants.SWITCH_BUTTON_KEY, false)
            if (!isDirectChatFragment) {
                updateUI(false)
            }
        } else {
            SharedPrefUtils.putPrefBoolean(Constants.SWITCH_BUTTON_KEY, true)
            if (!isDirectChatFragment) {
                updateUI(true)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed(
            { isUpdatingUI = false },
            300
        )
    }
}