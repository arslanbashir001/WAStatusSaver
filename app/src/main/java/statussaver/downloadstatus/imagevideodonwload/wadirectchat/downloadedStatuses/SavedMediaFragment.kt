package statussaver.downloadstatus.imagevideodonwload.wadirectchat.downloadedStatuses

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.FragmentSavedMediaBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.MediaModel
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.adapters.MediaAdapter
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.bottomSheet.BottomSheetGuide


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
            val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            when (nightModeFlags) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    // Dark mode is active
                    binding.layoutGuide.img.setImageResource(R.drawable.vector_guide_dark)
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    // Light mode is active
                    binding.layoutGuide.img.setImageResource(R.drawable.vector_guide_light)
                }
            }

            binding.mediaRecyclerView.visibility = GONE
            binding.emptyTextView.alpha = 0f
            binding.emptyTextView.animate()
                .alpha(1f)
                .setDuration(1000)
                .start()
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