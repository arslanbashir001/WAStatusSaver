package com.arslan.statussaver.downloadedStatuses

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arslan.statussaver.databinding.FragmentSavedMediaBinding
import com.arslan.statussaver.models.MediaModel
import com.arslan.statussaver.views.adapters.MediaAdapter
import com.arslan.statussaver.views.bottomSheet.BottomSheetGuide


class SavedMediaFragment : Fragment() {

    private lateinit var binding: FragmentSavedMediaBinding
    private var mediaList: ArrayList<MediaModel> = arrayListOf()
    private lateinit var mediaAdapter: MediaAdapter

    companion object {
        private const val ARG_MEDIA_LIST = "media_list"
        fun newInstance(mediaList: ArrayList<MediaModel>): SavedMediaFragment {
            val fragment = SavedMediaFragment()
            val args = Bundle()
            args.putSerializable(ARG_MEDIA_LIST, mediaList)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedMediaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            mediaList = it.getSerializable(ARG_MEDIA_LIST) as ArrayList<MediaModel>
            Log.d("SavedMediaFragment", "onViewCreated: Media list size = ${mediaList.size}")
        }
            setupRecyclerView()
        binding.mediaRecyclerView.scheduleLayoutAnimation()


    }

    private fun setupRecyclerView() {
        mediaAdapter = MediaAdapter(mediaList, isDownloadedStatuses = true)
        binding.mediaRecyclerView.apply {
            adapter = mediaAdapter
        }

        // Show or hide the TextView based on the mediaList size
        if (mediaList.isEmpty()) {
            binding.emptyTextView.visibility = VISIBLE
            binding.mediaRecyclerView.visibility = GONE

            binding.layoutGuide.btnHowToUse.setOnClickListener {
                val bottomSheetGuide = BottomSheetGuide()
                fragmentManager?.let { it1 -> bottomSheetGuide.show(it1, "") }
            }

        } else {
            binding.emptyTextView.visibility = GONE
            binding.mediaRecyclerView.visibility = VISIBLE
        }



    }
}