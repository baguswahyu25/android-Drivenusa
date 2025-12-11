package projectichif.DriveNusa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import projectichif.DriveNusa.data.NotificationPreferences

@Suppress("UNCHECKED_CAST")
class PengaturanNotifVMFactory(
    private val prefs: NotificationPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PengaturanNotifViewModel::class.java)) {
            return PengaturanNotifViewModel(prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}