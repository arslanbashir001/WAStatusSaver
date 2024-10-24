package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.fragments


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.data.StatusRepo
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.FragmentMediaBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.MediaModel
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.Constants
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.isStatusExist
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.viewmodels.StatusViewModel
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.viewmodels.factories.StatusViewModelFactory
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.adapters.MediaAdapter


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
            updateAdapter(existingImages, true)
        }
    }

    private fun observeDownloadedVideos() {
        viewModel.downloadedVideosLiveData.observe(viewLifecycleOwner) { videosList ->
            val existingVideos = videosList.filter { mediaModel -> requireContext().isStatusExist(mediaModel.fileName) }
            updateAdapter(existingVideos, true)
        }
    }

    private fun observeWhatsAppImages() {
        viewModel.whatsAppImagesLiveData.observe(viewLifecycleOwner) { unFilteredList ->
            updateAdapter(unFilteredList, false)
            binding.imgNoMedia.setImageResource(R.drawable.ic_no_image_media)
            Log.d("isExisst", "observeWhatsAppImages: " + "no wa images")
        }
    }

    private fun observeWhatsAppVideos() {
        viewModel.whatsAppVideosLiveData.observe(viewLifecycleOwner) { unFilteredList ->
            updateAdapter(unFilteredList, false)
            binding.imgNoMedia.setImageResource(R.drawable.ic_no_video_media)
            Log.d("isExisst", "observeWhatsAppImages: " + "no wa videos")
        }
    }

    private fun observeWhatsAppBusinessImages() {
        viewModel.whatsAppBusinessImagesLiveData.observe(viewLifecycleOwner) { unFilteredList ->
            updateAdapter(unFilteredList, false)
            binding.imgNoMedia.setImageResource(R.drawable.ic_no_image_media)
            Log.d("isExisst", "observeWhatsAppImages: " + "no wa business images")
        }
    }

    private fun observeWhatsAppBusinessVideos() {
        viewModel.whatsAppBusinessVideosLiveData.observe(viewLifecycleOwner) { unFilteredList ->
            updateAdapter(unFilteredList, false)
            binding.imgNoMedia.setImageResource(R.drawable.ic_no_video_media)
            Log.d("isExisst", "observeWhatsAppImages: " + "no wa business videos")
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
//        setupObservers()
        Handler(Looper.getMainLooper()).post {
            if (adapter !=null){
                adapter.notifyDataSetChanged()

            }
        }

    }
}