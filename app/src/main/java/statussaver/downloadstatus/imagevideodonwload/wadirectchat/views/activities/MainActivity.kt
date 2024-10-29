package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.activities


import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.customSwtich.IconSwitch
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.customSwtich.IconSwitch.Checked
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.ActivityMainBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.Constants
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefUtils
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.adapters.ViewPagerAdapter
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.bottomSheet.BottomSheetPermission
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.bottomSheet.BottomSheetUpdate

class MainActivity : AppCompatActivity(), IconSwitch.CheckedChangeListener {

    private lateinit var binding: ActivityMainBinding
    private var isDirectChatFragment: Boolean = false
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().getReference("LatestVersionCode")

        checkForUpdate()

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

    override fun onBackPressed() {
        when (binding.viewPager.currentItem) {
            0 -> {
                // simple whatsapp
                super.onBackPressed()
                finishAffinity()
            }

            1 -> {
                // whatsapp business
                finishAffinity()
            }

            else -> {
                // others like downloaded status and direct chat fragment.
                binding.viewPager.setCurrentItem(0, true) // Animate the transition
                binding.navigationView.selectedItemId =
                    R.id.menu_status // Set the first item as selected
            }
        }
    }
    private fun checkForUpdate() {
        val currentVersionCode = getCurrentVersionCode()

        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val latestVersionCode = snapshot.getValue(Long::class.java) ?: 0L
                if (latestVersionCode > currentVersionCode) {
                    showUpdateAvailableDialog()
//                    Toast.makeText(this, "Update available!", Toast.LENGTH_SHORT).show()
                } else {
//                    Toast.makeText(this, "You are up to date.", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
//            Toast.makeText(this, "Failed to fetch version code.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentVersionCode(): Int {
        return try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            0 // Fallback version code
        }
    }

    private fun showUpdateAvailableDialog() {
        val bottomSheet = BottomSheetUpdate()
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

}