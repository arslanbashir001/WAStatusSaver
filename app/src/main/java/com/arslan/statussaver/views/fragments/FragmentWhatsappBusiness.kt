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
import com.arslan.statussaver.databinding.FragmentWhatsappBusinessBinding
import com.arslan.statussaver.utils.Constants
import com.arslan.statussaver.utils.SharedPrefKeys
import com.arslan.statussaver.utils.SharedPrefUtils
import com.arslan.statussaver.utils.installWhatsApp
import com.arslan.statussaver.utils.isAppInstalled
import com.arslan.statussaver.viewmodels.StatusViewModel
import com.arslan.statussaver.viewmodels.factories.StatusViewModelFactory
import com.arslan.statussaver.views.adapters.MediaViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator





class FragmentWhatsappBusiness : Fragment() {

    private lateinit var binding: FragmentWhatsappBusinessBinding

    private var viewPagerTitles: ArrayList<String> = arrayListOf()
    private lateinit var viewModel: StatusViewModel

    private lateinit var folderPermissionLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWhatsappBusinessBinding.inflate(inflater, container, false)
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


        if (context?.isAppInstalled(Constants.TYPE_WHATSAPP_BUSINESS) == true) {
            setupBusinessWhatsAppStatuses()
            binding.appNotInstalledLayoutHolder.visibility = GONE
        } else {
            binding.appNotInstalledLayoutHolder.visibility = VISIBLE
            binding.layoutAppNotInstalled.imgAppNotInstalled.setImageResource(R.drawable.vector_wp_business_not_installed)
            binding.layoutAppNotInstalled.tvAppNotInstalled.text =
                getString(R.string.whatsapp_business_not_installed)
            binding.layoutAppNotInstalled.btnInstallApp.text =
                getString(R.string.Install_whatsApp_business)
            Log.d("isWhatsappInstalled", "onResume: " + "Business Whatsapp not installed")
        }


        if (activity?.contentResolver?.persistedUriPermissions?.size!! != 0) {
            if (activity?.contentResolver?.persistedUriPermissions?.get(0)?.uri?.path?.contains("Business")!!) {
                Log.d(tag, "onViewCreated: " + "this is whatsApp business permission")
            } else {
                registerFolderPermissionCallback()
            }
        } else {
            registerFolderPermissionCallback()
        }

        binding.layoutAppNotInstalled.btnInstallApp.setOnClickListener {
            context?.installWhatsApp(Constants.TYPE_WHATSAPP_BUSINESS)
        }
    }

    private fun requestFolderPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, Constants.getWhatsappBusinessUri())
                putExtra("android.content.extra.SHOW_ADVANCED", true)
            }
            folderPermissionLauncher.launch(intent)
        }
    }

    private fun registerFolderPermissionCallback() {
        folderPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    result.data?.data?.let { treeUri ->
                        // Take persistable URI permission
                        requireActivity().contentResolver.takePersistableUriPermission(
                            treeUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                        // Save the permission and URI in SharedPreferences
                        SharedPrefUtils.putPrefString(
                            SharedPrefKeys.PREF_KEY_WP_BUSINESS_TREE_URI,
                            treeUri.toString()
                        )
                        SharedPrefUtils.putPrefBoolean(
                            SharedPrefKeys.PREF_KEY_WP_BUSINESS_PERMISSION_GRANTED,
                            true
                        )
                        getWhatsAppBusinessStatuses()
                    }
                }
            }
    }

    private fun setupBusinessWhatsAppStatuses() {
        if (activity?.contentResolver?.persistedUriPermissions?.size!! != 0) {
            if (activity?.contentResolver?.persistedUriPermissions?.get(0)?.uri?.path?.contains("Business")!!) {
                Log.d(
                    "permissionCheck",
                    "setupBusinessWhatsAppStatuses: " + "whatsapp business have permission"
                )
                getWhatsAppBusinessStatuses()
            } else {
//                binding.permissionLayoutHolder.visibility = VISIBLE
                showPermissionLayout()
                Log.d(
                    "permissionCheck",
                    "setupBusinessWhatsAppStatuses: " + "whatsapp business not have permission"
                )
            }
        } else {
            showPermissionLayout()
//            binding.permissionLayoutHolder.visibility = VISIBLE
            Log.d(
                "permissionCheck",
                "setupBusinessWhatsAppStatuses: " + "whatsapp business not have permission 2"
            )
        }

        binding.permissionLayout.btnPermission.setOnClickListener {
            requestFolderPermission()
        }


        if (binding.statusViewPager.adapter == null) {
            val viewPagerAdapter = MediaViewPagerAdapter(
                requireActivity(),
                imagesType = Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES,
                videosType = Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS
            )
            binding.statusViewPager.adapter = viewPagerAdapter
            TabLayoutMediator(binding.tabLayout, binding.statusViewPager) { tab, pos ->
                tab.text = viewPagerTitles[pos]
            }.attach()
        }
    }

    private fun showPermissionLayout(){
        binding.permissionLayoutHolder.visibility = VISIBLE
//        binding.permissionLayout.tvAllowPermission.text = "Allow Permission"
//        binding.permissionLayout.tvPermissionDescription.text = ""
        binding.permissionLayout.imgAllowPermission.setImageResource(R.drawable.image_allow_permit_wp_business)
    }

    private val tag = "FragmentStatus"
    private fun getWhatsAppBusinessStatuses() {
        // function to get wp statuses
        binding.permissionLayoutHolder.visibility = GONE
        Log.d(tag, "getWhatsAppBusinessStatuses: Getting Wp Business Statuses")
        viewModel.getWhatsAppBusinessStatuses()
    }

    override fun onResume() {
        super.onResume()

//        Log.d("resumee", "onResume: " + "whatsApp business")

//        val repo = StatusRepo(requireActivity())
//        viewModel = ViewModelProvider(
//            requireActivity(),
//            StatusViewModelFactory(repo)
//        )[StatusViewModel::class.java]
//
//        Handler(Looper.getMainLooper()).post {
//            if (context?.isAppInstalled(Constants.TYPE_WHATSAPP_BUSINESS) == true) {
//                setupBusinessWhatsAppStatuses()
//                binding.appNotInstalledLayoutHolder.visibility = GONE
//            } else {
//                binding.appNotInstalledLayoutHolder.visibility = VISIBLE
//                binding.layoutAppNotInstalled.tvAppNotInstalled.text =
//                    getString(R.string.whatsapp_business_not_installed)
//                binding.layoutAppNotInstalled.btnInstallApp.text =
//                    getString(R.string.Install_whatsApp_business)
//                Log.d("isWhatsappInstalled", "onResume: " + "Business Whatsapp not installed")
//            }
//        }


    }
}