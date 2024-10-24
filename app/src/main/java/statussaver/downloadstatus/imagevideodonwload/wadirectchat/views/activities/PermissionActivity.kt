package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.ActivityPermissionBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.bottomSheet.BottomSheetPermission

class PermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionBinding
    private val READ_WRITE_PERMISSION_CODE = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.btnAllowPermission.setOnClickListener {
            requestReadWritePermission()
        }
    }

    // Check if a specific permission is granted
    private fun checkPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request read and write permissions if not granted
    private fun requestReadWritePermission() {
        val permissionsToRequest = mutableListOf<String>()

        if (!checkPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!checkPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                READ_WRITE_PERMISSION_CODE
            )
        } else {
            Toast.makeText(this, "All permissions are already granted.", Toast.LENGTH_SHORT).show()
            goToMainActivity()
        }
    }

    // Show a dialog requesting the user to enable permissions in settings
    private fun showPermissionRequestDialog() {
        val bottomSheet = BottomSheetPermission()
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }


    // Handle the result of permission requests
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_WRITE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                val grantedPermissions =
                    grantResults.count { it == PackageManager.PERMISSION_GRANTED }
                if (grantedPermissions == grantResults.size) {
                    Toast.makeText(baseContext, "Permissions granted", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                } else {
                    if (grantResults.any { it == PackageManager.PERMISSION_DENIED }) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        ) {
                            Toast.makeText(this, "Permissions are necessary", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            showPermissionRequestDialog()
                        }
                    }
                }
            }
        }
    }

    // Check permissions when user returns to the activity from settings
    override fun onResume() {
        super.onResume()

        if (checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
            checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            // All required permissions are granted, navigate to MainActivity
            goToMainActivity()
        }
    }

    // Method to navigate to MainActivity
    private fun goToMainActivity() {
        startActivity(Intent(this@PermissionActivity, MainActivity::class.java))
        finish() // Optionally finish this activity
    }
}

