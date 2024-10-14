package com.arslan.statussaver.views.adapters

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.arslan.statussaver.R
import com.arslan.statussaver.models.MEDIA_TYPE_IMAGE
import com.arslan.statussaver.models.MediaModel
import com.arslan.statussaver.utils.Constants
import com.arslan.statussaver.utils.isStatusExist
import com.arslan.statussaver.utils.saveStatus
import com.arslan.statussaver.views.activities.ImagesPreview
import com.arslan.statussaver.views.activities.VideosPreview
import com.bumptech.glide.Glide
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
        val mediaModel = list[position]

        val file = File(mediaModel.pathUri)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            Log.d("versionCheck", "onBindViewHolder: " + "before q " + mediaModel.pathUri)
            val fileUri = Uri.fromFile(file)
            Glide.with(holder.itemView.context)
                .load(fileUri)
                .into(holder.statusImage)
        } else {
            Log.d("versionCheck", "onBindViewHolder: " + "after q" + mediaModel.pathUri)
            if (mediaModel.pathUri.contains("content://")) {
                Uri.fromFile(file)
                Glide.with(holder.itemView.context)
                    .load(mediaModel.pathUri)
                    .into(holder.statusImage)
            } else {
                val fileUri = Uri.fromFile(file)
                Glide.with(holder.itemView.context)
                    .load(fileUri)
                    .into(holder.statusImage)
            }
        }

        if (mediaModel.type == MEDIA_TYPE_IMAGE) {
            holder.statusPlay.visibility = GONE
        }

        if (isDownloadedStatuses) {
            holder.statusDownload.visibility = GONE
        } else {
            holder.statusDownload.visibility = VISIBLE

            Log.d("isDowloaded", "onBindViewHolder: " + holder.itemView.context.isStatusExist(mediaModel.fileName) )

            val downloadImage = if (holder.itemView.context.isStatusExist(mediaModel.fileName)) {
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
            intent.putExtra(Constants.MEDIA_MODEL, mediaModel)

            if (mediaModel.type == MEDIA_TYPE_IMAGE) {
                intent.setClass(holder.itemView.context, ImagesPreview::class.java)
            } else {
                intent.setClass(holder.itemView.context, VideosPreview::class.java)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.statusDownload.setOnClickListener {
            if (holder.itemView.context.isStatusExist(mediaModel.fileName)) {
                Toast.makeText(holder.itemView.context, "File already exists", Toast.LENGTH_SHORT).show()
            } else {
                val isDownloaded = holder.itemView.context.saveStatus(mediaModel)
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

    private fun setFadeAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 20
        view.startAnimation(anim)
    }
}