package projectichif.DriveNusa.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import projectichif.DriveNusa.data.NotificationPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PengaturanNotifViewModel(
    private val prefs: NotificationPreferences
) : ViewModel() {

    val pengingat = prefs.pengingat.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val pembaruanAplikasi = prefs.pembaruanAplikasi.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val pembaruanProduk = prefs.pembaruanProduk.stateIn(viewModelScope, SharingStarted.Lazily, false)
    val promo = prefs.promo.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun setPengingat(value: Boolean) = viewModelScope.launch {
        prefs.setPengingat(value)
    }

    fun setPembaruanAplikasi(value: Boolean) = viewModelScope.launch {
        prefs.setPembaruanAplikasi(value)
    }

    fun setPembaruanProduk(value: Boolean) = viewModelScope.launch {
        prefs.setPembaruanProduk(value)
    }

    fun setPromo(value: Boolean) = viewModelScope.launch {
        prefs.setPromo(value)
    }
}