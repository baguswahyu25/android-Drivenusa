package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import projectichif.DriveNusa.databinding.ActivityDetailPemesananBinding
import java.text.NumberFormat

class DetailPemesananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPemesananBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        val id = intent.getIntExtra("id", 0)
        loadDetail(id)
    }

    private fun loadDetail(id: Int) {
        lifecycleScope.launch {
            val response = ApiClient.authApi.getDetailPemesanan(id)
            if (response.isSuccessful && response.body() != null) {

                val data = response.body()!!

                // Header
                binding.tvJudul.text = data.judul
                binding.tvPaket.text = data.paket
                binding.tvHarga.text =
                    "Rp ${NumberFormat.getInstance().format(data.harga)}"

                // Detail pembayaran
                binding.tvNamaPengirim.text = "order id : ${data.orderId}"
                binding.tvNamaPenerima.text = "nama penerima : dhuha group"
                binding.tvTanggalBayar.text = "tanggal bayar : ${data.tanggal}"
                binding.tvMetodeBayar.text =
                    "metode pembayaran : ${data.metodePembayaran}"

                // Status
                binding.tvStatus.text = data.paymentStatus

                if (data.status == "PAID") {
                    binding.tvStatus.setBackgroundResource(R.drawable.bg_status_paid)
                } else {
                    binding.tvStatus.setBackgroundResource(R.drawable.bg_status_pending)
                }
            }
        }
    }
}





