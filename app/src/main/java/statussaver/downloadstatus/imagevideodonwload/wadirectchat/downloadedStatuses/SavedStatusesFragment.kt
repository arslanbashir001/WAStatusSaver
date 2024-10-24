package statussaver.downloadstatus.imagevideodonwload.wadirectchat.downloadedStatuses

import android.content.ContentUris
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.FragmentSavedStatusesBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.MEDIA_TYPE_IMAGE
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.MEDIA_TYPE_VIDEO
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.MediaModel
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.getFileExtension
import java.io.File

class SavedStatusesFragment : Fragment() {

    private lateinit var binding: FragmentSavedStatusesBinding
    private var viewPagerTitles: ArrayList<String> = arrayListOf()
    private val downloadedStatusesList = mutableListOf<MediaModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedStatusesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPagerTitles = arrayListOf(getString(R.string.images), getString(R.string.videos))
        setupViewPagerWithTabs()
        getDownloadedStatuses()
        Log.d("down", "onViewCreated: ")
    }

    private fun setupViewPagerWithTabs() {
        val adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2 // Images and Videos

            override fun createFragment(position: Int): Fragment {
                val mediaType = if (position == 0) MEDIA_TYPE_IMAGE else MEDIA_TYPE_VIDEO
                val filteredList = downloadedStatusesList.filter { it.type == mediaType }
                return SavedMediaFragment.newInstance(ArrayList(filteredList))
            }
        }

        binding.statusViewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.statusViewPager) { tab, position ->
            tab.text = viewPagerTitles[position]
        }.attach()
    }

    private fun getDownloadedStatuses() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            getDownloadedStatusesForAndroid10AndBelow()
        } else {
            getDownloadedStatusesForAndroid11AndAbove()
        }
    }

    private fun getDownloadedStatusesForAndroid10AndBelow() {
        CoroutineScope(Dispatchers.IO).launch {
            val downloadedFolder = File(
                Environment.getExternalStorageDirectory().absolutePath,
                "Download/Status Saver"
            )
            downloadedStatusesList.clear()
            if (downloadedFolder.exists() && downloadedFolder.isDirectory) {
                downloadedFolder.listFiles()?.forEach { file ->
                    if (file.isFile && file.name != ".nomedia") {
                        val type = if (getFileExtension(file.name) == "mp4") {
                            MEDIA_TYPE_VIDEO
                        } else {
                            MEDIA_TYPE_IMAGE
                        }

                        val model = MediaModel(
                            pathUri = file.absolutePath,
                            fileName = file.name,
                            type = type,
                        )
                        downloadedStatusesList.add(model)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                setupViewPagerWithTabs()
            }
        }
    }

    private fun getDownloadedStatusesForAndroid11AndAbove() {
        CoroutineScope(Dispatchers.IO).launch {
            downloadedStatusesList.clear()

            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.RELATIVE_PATH
            )

            val selection = "${MediaStore.Files.FileColumns.RELATIVE_PATH} LIKE ?"
            val selectionArgs = arrayOf("%Download/Status Saver%")

            val queryUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)

            context?.contentResolver?.query(
                queryUri, projection, selection, selectionArgs, null
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val mimeTypeColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val mimeType = cursor.getString(mimeTypeColumn)
                    val contentUri = ContentUris.withAppendedId(queryUri, id)

                    val type = if (mimeType.startsWith("video")) {
                        MEDIA_TYPE_VIDEO
                    } else if (mimeType.startsWith("image")) {
                        MEDIA_TYPE_IMAGE
                    } else {
                        continue // Skip non-image/video files
                    }

                    val model = MediaModel(
                        pathUri = contentUri.toString(),
                        fileName = name,
                        type = type,
                    )

                    downloadedStatusesList.add(model)
                }
            }

            withContext(Dispatchers.Main) {
                setupViewPagerWithTabs()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("down", "onResume: ")
//        Handler(Looper.getMainLooper()).postDelayed({
//            getDownloadedStatuses()
//        }, 200)

        Handler(Looper.getMainLooper()).post{
            getDownloadedStatuses()

        }
    }
}