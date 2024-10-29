package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.bottomSheet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.BottomSheetUpdateBinding

class BottomSheetUpdate : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetUpdateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetUpdateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        binding.btnUpdateLater.setOnClickListener {
            dismiss()
        }
        binding.btnUpdateNow.setOnClickListener {
            goToPlay()
        }
    }

    private fun goToPlay() {
        val uri = Uri.parse("http://play.google.com/store/apps/details?id=${context?.packageName}")
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}