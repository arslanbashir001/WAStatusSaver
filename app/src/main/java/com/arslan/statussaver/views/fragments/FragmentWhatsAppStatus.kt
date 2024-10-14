package com.arslan.statussaver.views.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.arslan.statussaver.R
import com.arslan.statussaver.data.StatusRepo
import com.arslan.statussaver.databinding.FragmentStatusBinding
import com.arslan.statussaver.utils.Constants
import com.arslan.statussaver.utils.SharedPrefKeys
import com.arslan.statussaver.utils.SharedPrefUtils
import com.arslan.statussaver.utils.installWhatsApp
import com.arslan.statussaver.utils.isAppInstalled
import com.arslan.statussaver.viewmodels.StatusViewModel
import com.arslan.statussaver.viewmodels.factories.StatusViewModelFactory
import com.arslan.statussaver.views.adapters.MediaViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator


class FragmentWhatsAppStatus : Fragment() {

    private lateinit var binding: FragmentStatusBinding
    private var viewPagerTitles: ArrayList<String> = arrayListOf()// Initialize later
    private lateinit var viewModel: StatusViewModel

    private lateinit var folderPermissionLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize viewPagerTitles when the context is available
        viewPagerTitles = arrayListOf(getString(R.string.images), getString(R.string.videos))

        val repo = StatusRepo(requireActivity())
        viewModel = ViewModelProvider(
            requireActivity(),
            StatusViewModelFactory(repo)
        )[StatusViewModel::class.java]


        if (context?.isAppInstalled(Constants.TYPE_WHATSAPP_MAIN) == true) {
            binding.appNotInstalledLayoutHolder.visibility = GONE
            setupWhatsappStatuses()
        } else {
            binding.appNotInstalledLayoutHolder.visibility = VISIBLE
            binding.layoutAppNotInstalled.imgAppNotInstalled.setImageResource(R.drawable.vector_whatsapp_not_installed)
            binding.layoutAppNotInstalled.tvAppNotInstalled.text =
                getString(R.string.whatsapp_not_installed)
            binding.layoutAppNotInstalled.btnInstallApp.text =
                getString(R.string.Install_whatsApp)

        }


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            getWhatsAppStatuses()
        } else {
            if (activity != null) {
                if (activity?.contentResolver?.persistedUriPermissions?.size!! <= 0) {
                    Log.d("permissionCheck", "onViewCreated: " + "Whatsapp permission not granted")
                    registerFolderPermissionCallback()
                } else {
                    if (activity?.contentResolver?.persistedUriPermissions?.size!! == 1) {
                        if (!activity?.contentResolver?.persistedUriPermissions?.get(0)?.uri?.path?.contains(
                                "Business"
                            )!!
                        ) {
                            Log.d(
                                "permissionCheck",
                                "onViewCreated: " + "whatsapp business have permission"
                            )
                            registerFolderPermissionCallback()
                        } else {
                            Log.d(
                                "permissionCheck",
                                "onViewCreated: " + "whatsapp business not have permission"
                            )
                            registerFolderPermissionCallback()
                        }
                    } else {
                        getWhatsAppStatuses()
                        Log.d(
                            "permissionCheck",
                            "onViewCreated: " + "whatsapp business not have permission 2"
                        )
                    }
                }
            }
        }

        binding.layoutAppNotInstalled.btnInstallApp.setOnClickListener {
            context?.installWhatsApp(Constants.TYPE_WHATSAPP_MAIN)
        }
    }


    private fun setupWhatsappStatuses() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            getWhatsAppStatuses()
        } else {
            if (activity != null) {
                if (activity?.contentResolver?.persistedUriPermissions?.size!! <= 0) {
                    Log.d(
                        "permissionCheck",
                        "setupWhatsappStatuses: " + "Whatsapp permission not granted"
                    )
                    showPermissionLayout()

                } else {
                    if (activity?.contentResolver?.persistedUriPermissions?.size!! == 1) {
                        if (!activity?.contentResolver?.persistedUriPermissions?.get(0)?.uri?.path?.contains(
                                "Business"
                            )!!
                        ) {
                            Log.d(
                                "permissionCheck",
                                "setupWhatsappStatuses: " + "whatsapp business have permission"
                            )
                            showPermissionLayout()
                        } else {
                            Log.d(
                                "permissionCheck",
                                "setupWhatsappStatuses: " + "whatsapp business not have permission"
                            )
                        }
                    } else {
                        showPermissionLayout()
                        Log.d(
                            "permissionCheck",
                            "setupWhatsappStatuses: " + "whatsapp business not have permission 2"
                        )
                    }
                }
            }
        }

        binding.permissionLayout.btnPermission.setOnClickListener {
            requestFolderPermission()
        }

        if (binding.statusViewPager.adapter == null) {
            // ViewPager Setup
            val viewPagerAdapter = activity?.let { MediaViewPagerAdapter(it) }
            binding.statusViewPager.adapter = viewPagerAdapter
            TabLayoutMediator(binding.tabLayout, binding.statusViewPager) { tab, pos ->
                tab.text = viewPagerTitles[pos]
            }.attach()
        }
    }

    private fun requestFolderPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, Constants.getWhatsappUri())
                putExtra("android.content.extra.SHOW_ADVANCED", true)
            }
            folderPermissionLauncher.launch(intent)
        }
    }

    private fun showPermissionLayout(){
        binding.permissionLayoutHolder.visibility = VISIBLE
        binding.permissionLayout.imgAllowPermission.setImageResource(R.drawable.image_allow_folder_permit_wp)
    }

    private fun registerFolderPermissionCallback() {

        folderPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    result.data?.data?.let { treeUri ->
                        // Take persistable URI permission
                        activity?.contentResolver?.takePersistableUriPermission(
                            treeUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        // Save the permission and URI in SharedPreferences
                        SharedPrefUtils.putPrefString(
                            SharedPrefKeys.PREF_KEY_WP_TREE_URI,
                            treeUri.toString()
                        )
                        SharedPrefUtils.putPrefBoolean(
                            SharedPrefKeys.PREF_KEY_WP_PERMISSION_GRANTED,
                            true
                        )
                        getWhatsAppStatuses()
                        binding.swipeRefreshLayout.visibility = VISIBLE
                    }
                }
            }
    }


    private fun getWhatsAppStatuses() {
        // function to get wp statuses
        binding.permissionLayoutHolder.visibility = GONE
        viewModel.getWhatsAppStatuses()
    }

    override fun onResume() {
        super.onResume()

        Log.d("resumee", "onResume: " + "whatsApp")
    }
}