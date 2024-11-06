package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.activities

import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.data.StatusRepo
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.ActivityImagesPreviewBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.MediaModel
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.Constants
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.deleteStatus
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.isStatusExist
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.repostStatus
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.saveStatus
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.shareStatus
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.viewmodels.StatusViewModel
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.viewmodels.factories.StatusViewModelFactory
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.adapters.ImagePreviewAdapter

class ImagesPreview : AppCompatActivity() {

    private val binding by lazy {
        ActivityImagesPreviewBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: StatusViewModel

    lateinit var adapter: ImagePreviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.preview_activity_background_color, null)
//        window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.preview_activity_background_color, null)
//        window.decorView.systemUiVisibility = 0

        val repo = StatusRepo(this@ImagesPreview)
        viewModel = ViewModelProvider(
            this@ImagesPreview,
            StatusViewModelFactory(repo)
        )[StatusViewModel::class.java]

        binding.apply {

            val list =
                intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as ArrayList<MediaModel>
            val scrollTo = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
            val isDownloadedStatuses = intent.getBooleanExtra(Constants.IS_DOWNLOADED_STATUSES, false)

            Log.d("isDown", "onCreate: $isDownloadedStatuses")

            adapter = ImagePreviewAdapter(list)
            imagesViewPager.adapter = adapter

            // Set the initial page
            if (list.isNotEmpty()) {
                imagesViewPager.currentItem = scrollTo
                updateUI(list[scrollTo], isDownloadedStatuses) // Initial UI update
            }

            // Set up page change callback for ViewPager2
            val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (list.isNotEmpty() && position < list.size) {
                        val mediaModel = list[position]
                        updateUI(mediaModel, isDownloadedStatuses)
                    }
                }
            }

            imagesViewPager.registerOnPageChangeCallback(pageChangeCallback)

            binding.btnBack.setOnClickListener { finish() }

            tools.delete.setOnClickListener {
                val currentItemPosition = imagesViewPager.currentItem
                val currentMedia = list[currentItemPosition]

                if (deleteStatus(currentMedia)) {
                    Toast.makeText(
                        this@ImagesPreview,
                        "Status deleted successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Remove the item from the list
                    list.removeAt(currentItemPosition)
                    adapter.notifyItemRemoved(currentItemPosition)

                    // Check if the list is empty
                    if (list.isNotEmpty()) {
                        // Move to the next item if possible
                        val nextPosition =
                            if (currentItemPosition < list.size) currentItemPosition else list.size - 1
                        imagesViewPager.setCurrentItem(nextPosition, true)

                        // Update the UI for the new item
                        val nextMediaModel = list[nextPosition]
                        updateUI(nextMediaModel, isDownloadedStatuses)
                    } else {
                        // If the list is empty, unregister the page change callback and finish the activity
                        imagesViewPager.unregisterOnPageChangeCallback(pageChangeCallback)
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this@ImagesPreview,
                        "Unable to delete status",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            tools.download.setOnClickListener {
                val currentMedia = list[imagesViewPager.currentItem]

                if (isStatusExist(currentMedia.fileName)){
                    Toast.makeText(baseContext, "File already exists", Toast.LENGTH_SHORT).show()
                }else{
                    val isDownloaded = saveStatus(currentMedia)
                    if (isDownloaded) {
                        Toast.makeText(this@ImagesPreview, "Saved", Toast.LENGTH_SHORT).show()
                        tools.statusDownload.setImageResource(R.drawable.ic_downloaded)
                    } else {
                        Toast.makeText(this@ImagesPreview, "Unable to Save", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            toolBar.setOnClickListener {
                finish()
            }

            tools.layoutRepost.setOnClickListener {
                val currentMedia = list[imagesViewPager.currentItem]
                Log.d("Uri", "onCreate: " + currentMedia.pathUri.toUri())
                repostStatus(currentMedia.pathUri.toUri())
            }

            tools.layoutShare.setOnClickListener {
                val currentMedia = list[imagesViewPager.currentItem]
                Log.d("Uri", "onCreate: " + currentMedia.pathUri.toUri())
                shareStatus(currentMedia.pathUri.toUri())
            }
        }
    }

    private fun updateUI(mediaModel: MediaModel, isDownloadedStatuses: Boolean) {
        binding.apply {
            if (isDownloadedStatuses) {
                tools.delete.visibility = VISIBLE
                tools.download.visibility = GONE
            } else {
                tools.delete.visibility = GONE
                tools.download.visibility = VISIBLE
                val downloadImage = if (binding.root.context.isStatusExist(mediaModel.fileName)) {
                    R.drawable.ic_downloaded
                } else {
                    R.drawable.ic_download
                }
                tools.statusDownload.setImageResource(downloadImage)
            }
        }
    }
}