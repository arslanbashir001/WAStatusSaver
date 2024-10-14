package com.arslan.statussaver.viewmodels

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arslan.statussaver.data.StatusRepo
import com.arslan.statussaver.models.MEDIA_TYPE_IMAGE
import com.arslan.statussaver.models.MEDIA_TYPE_VIDEO
import com.arslan.statussaver.models.MediaModel
import com.arslan.statussaver.utils.Constants
import com.arslan.statussaver.utils.saveStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatusViewModel(private val repo: StatusRepo) : ViewModel() {
    private val tag = "StatusViewModel"

    // LiveData
    val whatsAppImagesLiveData = MutableLiveData<List<MediaModel>>()
    val whatsAppVideosLiveData = MutableLiveData<List<MediaModel>>()
    val whatsAppBusinessImagesLiveData = MutableLiveData<List<MediaModel>>()
    val whatsAppBusinessVideosLiveData = MutableLiveData<List<MediaModel>>()
    val downloadedImagesLiveData = MutableLiveData<List<MediaModel>>()
    val downloadedVideosLiveData = MutableLiveData<List<MediaModel>>()

    init {
        // Observe LiveData in init block to update media lists when data changes
        observeLiveData()
    }

    private fun observeLiveData() {
        repo.whatsAppStatusesLiveData.observeForever { mediaList ->
            updateMediaLiveData(mediaList, whatsAppImagesLiveData, whatsAppVideosLiveData)
        }
        repo.whatsAppBusinessStatusesLiveData.observeForever { mediaList ->
            updateMediaLiveData(mediaList, whatsAppBusinessImagesLiveData, whatsAppBusinessVideosLiveData)
        }
        repo.downloadedStatusesLiveData.observeForever { mediaList ->
            updateDownloadedMedia(mediaList)
        }
    }

    private fun updateMediaLiveData(mediaList: List<MediaModel>, imageLiveData: MutableLiveData<List<MediaModel>>, videoLiveData: MutableLiveData<List<MediaModel>>) {
        val (images, videos) = mediaList.partition { it.type == MEDIA_TYPE_IMAGE }
        imageLiveData.postValue(images)
        videoLiveData.postValue(videos)
    }

    fun getWhatsAppStatuses() {
        fetchStatuses { repo.getAllStatuses() }
    }

    fun getWhatsAppBusinessStatuses() {
        fetchStatuses { repo.getAllStatuses(Constants.TYPE_WHATSAPP_BUSINESS) }
    }

    fun getDownloadedStatuses() {
        fetchDownloadedStatuses { repo.getDownloadedStatuses() }
    }

    private fun fetchStatuses(fetch: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(tag, "Fetching statuses")
                fetch()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchDownloadedStatuses(fetch: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(tag, "Fetching downloaded statuses")
                fetch()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateDownloadedMedia(mediaList: List<MediaModel>) {
        val (images, videos) = mediaList.filterNotNull().partition { it.type == MEDIA_TYPE_IMAGE }
        downloadedImagesLiveData.postValue(images)
        downloadedVideosLiveData.postValue(videos)

        // Logging for debugging
        images.forEach { Log.d("downloaded", "Image: ${it.fileName}") }
        videos.forEach { Log.d("downloaded", "Video: ${it.fileName}") }
    }
}