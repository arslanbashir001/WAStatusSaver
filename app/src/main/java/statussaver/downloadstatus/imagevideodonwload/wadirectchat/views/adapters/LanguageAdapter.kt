package statussaver.downloadstatus.imagevideodonwload.wadirectchat.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.Language
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefKeys
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils.SharedPrefUtils

class LanguageAdapter(
    private val languages: List<Language>,
    private val onLanguageSelected: (Language) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private var selectedPosition = -1 // Initialize with -1 to indicate no selection

    init {
        // Get the stored language code from SharedPreferences
        val storedLanguageCode =
            SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_KEY_SELECTED_LANGUAGE_CODE, "")



        // Set the default selection to English if no language is stored or if the stored code is invalid
        selectedPosition = if (storedLanguageCode?.isEmpty() == true) {
            // Default to English
            getPositionForLanguageCode("en") // assuming "en" is the code for English
        } else {
            // Set the selected position based on the stored language code
            getPositionForLanguageCode(storedLanguageCode!!)
        }
    }

    // Function to get the position of a language by its code
    private fun getPositionForLanguageCode(code: String): Int {
        for (i in languages.indices) {
            if (languages[i].code == code) {
                return i
            }
        }
        return -1 // Return -1 if not found
    }

    inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flag: ImageView = itemView.findViewById(R.id.imgLanguageFlag)
        val languageText: TextView = itemView.findViewById(R.id.tvLanguageName)
        val radioButton: RadioButton = itemView.findViewById(R.id.btnRadio)

        init {
            radioButton.setOnClickListener {
                // Update selected position and notify
                notifyItemChanged(selectedPosition) // Deselect previously selected
                selectedPosition = adapterPosition // Update the selected position
                notifyItemChanged(selectedPosition) // Select the new item
                onLanguageSelected(languages[adapterPosition]) // Notify the selected language
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_language, parent, false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]
        holder.flag.setImageResource(language.flagResId)
        holder.languageText.text = language.name
        holder.radioButton.isChecked =
            position == selectedPosition // Check if this item is selected
    }

    override fun getItemCount() = languages.size
}