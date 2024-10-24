package statussaver.downloadstatus.imagevideodonwload.wadirectchat.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.data.StatusRepo
import statussaver.downloadstatus.imagevideodonwload.wadirectchat.viewmodels.StatusViewModel


class StatusViewModelFactory(private val repo: StatusRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatusViewModel(repo) as T
    }
}