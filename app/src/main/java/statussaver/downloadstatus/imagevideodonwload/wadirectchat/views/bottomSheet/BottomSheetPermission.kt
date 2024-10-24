package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.bottomSheet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.BuildConfig
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.BottomSheetLayoutPermissionBinding

class BottomSheetPermission : BottomSheetDialogFragment() {

    private lateinit var binding : BottomSheetLayoutPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetLayoutPermissionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnAllow.setOnClickListener {
            gotoSettingsForPermission()
        }
    }

    private fun gotoSettingsForPermission() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        intent.data = uri
        startActivity(intent)
    }
}