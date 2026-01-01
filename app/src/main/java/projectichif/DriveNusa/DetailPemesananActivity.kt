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
                    setStatusAnimation(data.paymentStatus)
                }
            }
        }
        private fun setStatusAnimation(status: String) {
            when (status.uppercase()) {
                "PAID", "SETTLEMENT", "CAPTURE" -> {
                    binding.successAnim.setAnimation(R.raw.succes_payment)
                    binding.tvStatus.text = "PAID"
                    binding.tvStatus.setBackgroundResource(R.drawable.bg_status_paid)
                }

                "PENDING" -> {
                    binding.successAnim.setAnimation(R.raw.pending)
                    binding.tvStatus.text = "PENDING"
                    binding.tvStatus.setBackgroundResource(R.drawable.bg_status_pending)
                }

                "FAILED", "CANCEL", "EXPIRE" -> {
                    binding.successAnim.setAnimation(R.raw.failed)
                    binding.tvStatus.text = "FAILED"
                    binding.tvStatus.setBackgroundResource(R.drawable.bg_status_failed)
                }

                else -> {
                    binding.successAnim.setAnimation(R.raw.pending)
                    binding.tvStatus.text = status
                    binding.tvStatus.setBackgroundResource(R.drawable.bg_status_pending)
                }
            }

            binding.successAnim.playAnimation()
        }

    }





