package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.data.StatusRepo
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.FragmentDownloadedStatuesBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.Constants
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.viewmodels.StatusViewModel
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.viewmodels.factories.StatusViewModelFactory
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.adapters.MediaViewPagerAdapter

class FragmentDownloadedStatues : Fragment() {

    private lateinit var binding: FragmentDownloadedStatuesBinding
    private lateinit var viewPagerTitles: ArrayList<String>
    private lateinit var viewModel: StatusViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDownloadedStatuesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPagerTitles = arrayListOf(getString(R.string.images), getString(R.string.videos))
        setupViewModel()
        setupObservers()
    }

    private fun setupViewModel() {
        val repo = StatusRepo(requireActivity())
        viewModel = ViewModelProvider(requireActivity(), StatusViewModelFactory(repo))[StatusViewModel::class.java]
    }

    private fun setupObservers() {

        // Fetch statuses on a background thread
        viewModel.getDownloadedStatuses()

        viewModel.downloadedImagesLiveData.observe(viewLifecycleOwner) { images ->
            // Update the UI based on the statuses data
            setupDownloadedStatuses()
        }

        viewModel.downloadedVideosLiveData.observe(viewLifecycleOwner){videos ->
            setupDownloadedStatuses()

        }
    }

    private fun setupDownloadedStatuses() {
        if (binding.statusViewPager.adapter == null) {
            val viewPagerAdapter = MediaViewPagerAdapter(
                requireActivity(),
                imagesType = Constants.MEDIA_TYPE_DOWNLOADED_IMAGES,
                videosType = Constants.MEDIA_TYPE_DOWNLOADED_VIDEOS
            )

            binding.statusViewPager.adapter = viewPagerAdapter
            TabLayoutMediator(binding.tabLayout, binding.statusViewPager) { tab, pos ->
                tab.text = viewPagerTitles[pos]
            }.attach()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDownloadedStatuses()

    }
}