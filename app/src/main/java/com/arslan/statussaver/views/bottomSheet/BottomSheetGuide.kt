package com.arslan.statussaver.views.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arslan.statussaver.databinding.BottomSheetLayoutGuideBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetGuide : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetLayoutGuideBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetLayoutGuideBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false
        binding.btnGotIt.setOnClickListener { dismiss() }
    }
}
