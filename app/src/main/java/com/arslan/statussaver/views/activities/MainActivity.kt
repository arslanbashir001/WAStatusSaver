package com.arslan.statussaver.views.activities


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
import com.arslan.statussaver.R
import com.arslan.statussaver.customSwtich.IconSwitch
import com.arslan.statussaver.customSwtich.IconSwitch.Checked
import com.arslan.statussaver.databinding.ActivityMainBinding
import com.arslan.statussaver.utils.Constants
import com.arslan.statussaver.utils.SharedPrefKeys
import com.arslan.statussaver.utils.SharedPrefUtils
import com.arslan.statussaver.utils.slideFromStart
import com.arslan.statussaver.utils.slideToEndWithFadeOut
import com.arslan.statussaver.views.adapters.ViewPagerAdapter

class MainActivity : AppCompatActivity(),
    IconSwitch.CheckedChangeListener {
    private lateinit var binding: ActivityMainBinding
    private val PERMISSION_REQUEST_CODE = 50
    private var isDirectChatFragment: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.iconSwitch.setCheckedChangeListener(this)
        SharedPrefUtils.init(this)
        splashLogic()
        requestPermission()

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

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val isPermissionsGranted = SharedPrefUtils.getPrefBoolean(
                SharedPrefKeys.PREF_KEY_IS_PERMISSIONS_GRANTED,
                false
            )
            if (!isPermissionsGranted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
                Toast.makeText(this, "Please Grant Permissions", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val isGranted =
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            SharedPrefUtils.putPrefBoolean(
                SharedPrefKeys.PREF_KEY_IS_PERMISSIONS_GRANTED,
                isGranted
            )
        }
    }

    private fun splashLogic() {
        binding.apply {
            Handler(Looper.myLooper()!!).postDelayed({
                splashScreenHolder.slideToEndWithFadeOut()
                splashScreenHolder.visibility = View.GONE
            }, 2000)
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