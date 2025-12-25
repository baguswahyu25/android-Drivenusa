package projectichif.DriveNusa

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import projectichif.DriveNusa.api.PaketKursus
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class PaketKursusViewModel : ViewModel() {

    private val _paketKursus = MutableLiveData<List<PaketKursus>>()
    val paketKursus: LiveData<List<PaketKursus>> = _paketKursus

    fun loadPaketKursus() {
        // 🔒 jika data sudah ada, jangan load ulang
        if (_paketKursus.value != null) return

        viewModelScope.launch {
            try {
                val response = ApiClient.authApi.getPaketKursus()
                if (response.isSuccessful) {
                    val list = response.body()?.map {
                        PaketKursus(
                            nama = it.nama,
                            harga = "Rp. ${it.harga}",
                            image = "${ApiClient.BASE_IMAGE_URL}/${it.image}"
                        )
                    } ?: emptyList()

                    _paketKursus.postValue(list)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
