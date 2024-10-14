package com.arslan.statussaver.views.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.arslan.statussaver.R
import com.arslan.statussaver.databinding.ActivityVideosPreviewBinding
import com.arslan.statussaver.models.MediaModel
import com.arslan.statussaver.utils.Constants
import com.arslan.statussaver.utils.deleteStatus
import com.arslan.statussaver.utils.isStatusExist
import com.arslan.statussaver.utils.repostStatus
import com.arslan.statussaver.utils.saveStatus
import com.arslan.statussaver.utils.shareStatus
import com.arslan.statussaver.views.adapters.VideoPreviewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideosPreview : AppCompatActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityVideosPreviewBinding.inflate(layoutInflater)
    }
    lateinit var adapter: VideoPreviewAdapter
    private val TAG = "VideosPreview"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.preview_activity_background_color, null)
        window.navigationBarColor = ResourcesCompat.getColor(resources, R.color.preview_activity_background_color, null)
        window.decorView.systemUiVisibility = 0

        binding.apply {
            val list =
                intent.getSerializableExtra(Constants.MEDIA_LIST_KEY) as ArrayList<MediaModel>
            val scrollTo = intent.getIntExtra(Constants.MEDIA_SCROLL_KEY, 0)
            val isDownloadedStatuses = intent.getBooleanExtra(Constants.IS_DOWNLOADED_STATUSES, false)

            Log.d("isDown", "onCreate VideosPreview: $isDownloadedStatuses")


            adapter = VideoPreviewAdapter(list, activity)
            videoRecyclerView.adapter = adapter
            val pageSnapHelper = PagerSnapHelper()
            pageSnapHelper.attachToRecyclerView(videoRecyclerView)
            videoRecyclerView.scrollToPosition(scrollTo)

            if (list.isNotEmpty()) {
                Log.d("isDown", "onCreate VideosPreview 1: $isDownloadedStatuses")
                updateUI(list[scrollTo], isDownloadedStatuses) // Initial UI update
            }

            videoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val currentPosition = layoutManager.findFirstVisibleItemPosition()

                    // Ensure that currentPosition is valid
                    if (currentPosition != RecyclerView.NO_POSITION && currentPosition >= 0 && currentPosition < list.size) {
                        Log.d("isDown", "onCreate VideosPreview 2: $isDownloadedStatuses")
                        updateUI(list[currentPosition], isDownloadedStatuses)
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        Log.d(TAG, "onScrollStateChanged: Dragging")
                        stopAllPlayers()
                    }
                }
            })

            binding.btnBack.setOnClickListener { finish() }

            binding.tools.download.setOnClickListener {
                val currentMedia = list[scrollTo]

                if (isStatusExist(currentMedia.fileName)){
                    Toast.makeText(baseContext, "File already exists", Toast.LENGTH_SHORT).show()
                }else{
                    val isDownloaded = saveStatus(currentMedia)
                    if (isDownloaded) {
                        // Status is downloaded
                        Toast.makeText(this@VideosPreview, "Saved", Toast.LENGTH_SHORT).show()
                        tools.statusDownload.setImageResource(R.drawable.ic_downloaded)
                    } else {
                        // Unable to download status
                        Toast.makeText(this@VideosPreview, "Unable to Save", Toast.LENGTH_SHORT).show()
                    }
                }


            }

            tools.layoutRepost.setOnClickListener {
                repostStatus(list[scrollTo].pathUri.toUri())
            }

            tools.layoutShare.setOnClickListener {
                shareStatus(list[scrollTo].pathUri.toUri())
            }


            tools.delete.setOnClickListener {
                val currentItemPosition =    (binding.videoRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                val currentMedia = list[currentItemPosition]

                if (deleteStatus(currentMedia)) {
                    Toast.makeText(
                        this@VideosPreview,
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

                        binding.videoRecyclerView.scrollToPosition(nextPosition)
                        // Update the UI for the new item
                        val nextMediaModel = list[nextPosition]
                        updateUI(nextMediaModel, isDownloadedStatuses)

                    } else {
                        // If the list is empty, unregister the page change callback and finish the activity
//                        Toast.makeText(
//                            this@VideosPreview,
//                            "No more videos, closing preview.",
//                            Toast.LENGTH_SHORT
//                        ).show()
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this@VideosPreview,
                        "Unable to delete status",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

    private fun stopAllPlayers() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.apply {
                    for (i in 0 until videoRecyclerView.childCount) {
                        val child = videoRecyclerView.getChildAt(i)
                        val viewHolder = videoRecyclerView.getChildViewHolder(child)
                        if (viewHolder is VideoPreviewAdapter.ViewHolder) {
                            viewHolder.stopPlayer()
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        stopAllPlayers()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAllPlayers()
    }
}