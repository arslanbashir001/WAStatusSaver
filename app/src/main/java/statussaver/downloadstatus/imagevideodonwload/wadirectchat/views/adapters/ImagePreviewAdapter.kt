package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.adapters

import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.databinding.ItemImagePreviewBinding
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.MediaModel
import java.io.File

class ImagePreviewAdapter(
    private var list: ArrayList<MediaModel>
) : RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemImagePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            binding.apply {

                val file = File(mediaModel.pathUri)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    Log.d("versionCheck", "onBindViewHolder: " + "before q " + mediaModel.pathUri)
                    val fileUri = Uri.fromFile(file)
                    Glide.with(itemView.context)
                        .load(fileUri)
                        .into(zoomableImageView)
                } else {
                    Log.d("versionCheck", "onBindViewHolder: " + "after q" + mediaModel.pathUri)
                    if (mediaModel.pathUri.contains("content://")) {
                        Glide.with(itemView.context)
                            .load(mediaModel.pathUri)
                            .into(zoomableImageView)
                    } else {
                        val fileUri = Uri.fromFile(file)
                        Glide.with(itemView.context)
                            .load(fileUri)
                            .into(zoomableImageView)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImagePreviewAdapter.ViewHolder {
        val binding =
            ItemImagePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImagePreviewAdapter.ViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount() = list.size
}
