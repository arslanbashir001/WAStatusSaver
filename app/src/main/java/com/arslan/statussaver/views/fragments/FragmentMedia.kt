package com.arslan.statussaver.views.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.arslan.statussaver.data.StatusRepo
import com.arslan.statussaver.databinding.FragmentMediaBinding
import com.arslan.statussaver.models.MediaModel
import com.arslan.statussaver.utils.Constants
import com.arslan.statussaver.utils.isStatusExist
import com.arslan.statussaver.viewmodels.StatusViewModel
import com.arslan.statussaver.viewmodels.factories.StatusViewModelFactory
import com.arslan.statussaver.views.adapters.MediaAdapter
import com.arslan.statussaver.views.bottomSheet.BottomSheetGuide

class FragmentMedia : Fragment() {

    private lateinit var binding: FragmentMediaBinding
    private lateinit var viewModel: StatusViewModel
    private lateinit var adapter: MediaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the adapter with an empty list
        adapter = MediaAdapter(ArrayList(), false)
        binding.mediaRecyclerView.adapter = adapter
        binding.mediaRecyclerView.setHasFixedSize(true) // Improve performance if size is fixed

        setupObservers()
    }

    private fun setupObservers() {
        arguments?.let {
            val repo = StatusRepo(requireActivity())
            viewModel = ViewModelProvider(
                requireActivity(),
                StatusViewModelFactory(repo)
            )[StatusViewModel::class.java]

            val mediaType = it.getString(Constants.MEDIA_TYPE_KEY, "")

            // Show the ProgressBar initially before data is loaded
            binding.progressCircular.visibility = VISIBLE

            when (mediaType) {
                Constants.MEDIA_TYPE_WHATSAPP_IMAGES -> observeWhatsAppImages()
                Constants.MEDIA_TYPE_WHATSAPP_VIDEOS -> observeWhatsAppVideos()
                Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_IMAGES -> observeWhatsAppBusinessImages()
                Constants.MEDIA_TYPE_WHATSAPP_BUSINESS_VIDEOS -> observeWhatsAppBusinessVideos()
                Constants.MEDIA_TYPE_DOWNLOADED_IMAGES -> observeDownloadedImages()
                Constants.MEDIA_TYPE_DOWNLOADED_VIDEOS -> observeDownloadedVideos()
            }
        }
    }

    private fun observeDownloadedImages() {
        viewModel.downloadedImagesLiveData.observe(viewLifecycleOwner) { imagesList ->
            val existingImages = imagesList.filter { mediaModel -> requireContext().isStatusExist(mediaModel.fileName) }

            if (existingImages.isEmpty()) {
                binding.layoutGuideHolder.visibility = VISIBLE
            }

            binding.layoutGuide.btnHowToUse.setOnClickListener {
                val bottomSheetGuide = BottomSheetGuide()
                fragmentManager?.let { it1 -> bottomSheetGuide.show(it1, "") }
            }
            updateAdapter(existingImages, true)
        }
    }

    private fun observeDownloadedVideos() {
        viewModel.downloadedVideosLiveData.observe(viewLifecycleOwner) { videosList ->
            val existingVideos = videosList.filter { mediaModel -> requireContext().isStatusExist(mediaModel.fileName) }

            if (existingVideos.isEmpty()) {
                binding.layoutGuideHolder.visibility = VISIBLE
            }

            binding.layoutGuide.btnHowToUse.setOnClickListener {
                val bottomSheetGuide = BottomSheetGuide()
                fragmentManager?.let { it1 -> bottomSheetGuide.show(it1, "") }
            }
            updateAdapter(existingVideos, true)
        }
    }

    private fun observeWhatsAppImages() {
        viewModel.whatsAppImagesLiveData.observe(viewLifecycleOwner) { unFilteredList ->
            updateAdapter(unFilteredList, false)
        }
    }

    private fun observeWhatsAppVideos() {
        viewModel.whatsAppVideosLiveData.observe(viewLifecycleOwner) { unFilteredList ->
            updateAdapter(unFilteredList, false)
        }
    }

    private fun observeWhatsAppBusinessImages() {
        viewModel.whatsAppBusinessImagesLiveData.observe(viewLifecycleOwner) { unFilteredList ->
            updateAdapter(unFilteredList, false)
        }
    }

    private fun observeWhatsAppBusinessVideos() {
        viewModel.whatsAppBusinessVideosLiveData.observe(viewLifecycleOwner) { unFilteredList ->
            updateAdapter(unFilteredList, false)
        }
    }

    private fun updateAdapter(unFilteredList: List<MediaModel>, isDownloadedStatuses: Boolean) {
        Log.d("listSizeee", "updateAdapter: " + unFilteredList.size)
        val filteredList = ArrayList(unFilteredList.distinctBy { model -> model.fileName })

        adapter.updateData(filteredList, isDownloadedStatuses)

        // Hide ProgressBar and show/hide tempMediaText based on data
        binding.progressCircular.visibility = GONE
        binding.tempMediaText.visibility = if (filteredList.isEmpty()) VISIBLE else GONE
    }

    override fun onResume() {
        super.onResume()
        setupObservers()
    }
}