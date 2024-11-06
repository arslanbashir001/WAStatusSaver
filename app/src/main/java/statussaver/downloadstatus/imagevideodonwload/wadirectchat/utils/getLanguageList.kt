package statussaver.downloadstatus.imagevideodonwload.wadirectchat.utils

import statussaver.downloadstatus.imagevideodonwload.wadirectchat.R
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.models.Language

// Extension function to get the list of available languages
fun getLanguageList(): List<Language> {
    return listOf(
        Language("en", "English", R.drawable.flag_english),
        Language("es", "Spanish (Española)", R.drawable.flag_spanish),
        Language("ur", "Urdu (اردو)", R.drawable.flag_spanish),
        Language("hi", "Hindi (हिंदी)", R.drawable.flag_spanish),
        Language("fr", "French (Français)", R.drawable.flag_spanish),
        Language("de", "German (Deutsch)", R.drawable.flag_germany),
        Language("ru", "Russian (Русский)", R.drawable.flag_russia)
    )
}

// Extension function to get the language name by its code
fun List<Language>.getLanguageNameByCode(languageCode: String): String {
    // Find the language by code and return its name. Return "Unknown" if not found.
    return this.find { it.code == languageCode }?.name ?: "Unknown"
}
