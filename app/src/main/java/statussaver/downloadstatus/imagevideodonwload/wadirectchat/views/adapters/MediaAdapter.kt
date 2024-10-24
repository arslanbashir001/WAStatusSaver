package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.adapters

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.MEDIA_TYPE_IMAGE
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.MediaModel
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.Constants
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.isStatusExist
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.saveStatus
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.activities.ImagesPreview
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.activities.VideosPreview
import java.io.File


class MediaAdapter(
    private var list: ArrayList<MediaModel>,
    private var isDownloadedStatuses: Boolean
) : RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]

        val file = File(currentItem.pathUri)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.d("versionCheck", "onBindViewHolder: " + "before q " + currentItem.pathUri)
            val fileUri = Uri.fromFile(file)
            Glide.with(holder.itemView.context)
                .load(fileUri)
                .into(holder.statusImage)
        } else {
            Log.d("versionCheck", "onBindViewHolder: " + "after q" + currentItem.pathUri)
            if (currentItem.pathUri.contains("content://")) {
                Uri.fromFile(file)
                Glide.with(holder.itemView.context)
                    .load(currentItem.pathUri)
                    .into(holder.statusImage)
            } else {
                val fileUri = Uri.fromFile(file)
                Glide.with(holder.itemView.context)
                    .load(fileUri)
                    .into(holder.statusImage)
            }
        }

        if (currentItem.type == MEDIA_TYPE_IMAGE) {
            holder.statusPlay.visibility = GONE
        }

        if (isDownloadedStatuses) {
            holder.statusDownload.visibility = GONE
        } else {
            holder.statusDownload.visibility = VISIBLE

            Log.d("isDownloaded", "onBindViewHolder: " + holder.itemView.context.isStatusExist(currentItem.fileName) )

            val downloadImage = if (holder.itemView.context.isStatusExist(currentItem.fileName)) {
                R.drawable.ic_downloaded
            } else {
                R.drawable.ic_download
            }
            holder.statusDownload.setImageResource(downloadImage)
        }

        holder.cardStatus.setOnClickListener {
            val intent = Intent()
            intent.putExtra(Constants.MEDIA_LIST_KEY, list)
            intent.putExtra(Constants.MEDIA_SCROLL_KEY, position)
            intent.putExtra(Constants.IS_DOWNLOADED_STATUSES, isDownloadedStatuses)
            intent.putExtra(Constants.MEDIA_MODEL, currentItem)

            if (currentItem.type == MEDIA_TYPE_IMAGE) {
                intent.setClass(holder.itemView.context, ImagesPreview::class.java)
            } else {
                intent.setClass(holder.itemView.context, VideosPreview::class.java)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.statusDownload.setOnClickListener {
            if (holder.itemView.context.isStatusExist(currentItem.fileName)) {
                Toast.makeText(holder.itemView.context, "File already exists", Toast.LENGTH_SHORT).show()
            } else {
                val isDownloaded = holder.itemView.context.saveStatus(currentItem)
                if (isDownloaded) {
                    Toast.makeText(holder.itemView.context, "Saved", Toast.LENGTH_SHORT).show()
                    holder.statusDownload.setImageResource(R.drawable.ic_downloaded)
                } else {
                    Toast.makeText(holder.itemView.context, "Unable to Save", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun updateData(newList: ArrayList<MediaModel>, isDownloadedStatuses: Boolean) {
        list.clear()
        list.addAll(newList)
        this.isDownloadedStatuses = isDownloadedStatuses
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statusImage: ImageView = itemView.findViewById(R.id.status_image)
        val statusPlay: ImageView = itemView.findViewById(R.id.status_play)
        val statusDownload: ImageView = itemView.findViewById(R.id.status_download)
        val cardStatus: CardView = itemView.findViewById(R.id.card_status)
    }
}