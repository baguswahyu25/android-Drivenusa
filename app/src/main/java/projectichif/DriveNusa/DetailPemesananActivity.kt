    package projectichif.DriveNusa

    import android.content.Intent
    import android.os.Bundle
    import androidx.appcompat.app.AppCompatActivity
    import androidx.lifecycle.lifecycleScope
    import com.bumptech.glide.Glide
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
            binding.btnBeranda.setOnClickListener {
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }

            val id = intent.getIntExtra("id", 0)
            loadDetail(id)
        }

        private fun loadDetail(id: Int) {
            lifecycleScope.launch {
                val response = ApiClient.authApi.getDetailPemesanan(id)
                if (response.isSuccessful && response.body() != null) {

                    val data = response.body()!!
                    Glide.with(binding.imgMobil.context)
                        .load(data.image)
                        .placeholder(R.drawable.img_placeholder)
                        .into(binding.imgMobil)



                    // Header
                    binding.tvJudul.text = data.mobil
                    binding.tvPaket.text = data.paket
                    binding.tvHarga.text =
                        "Rp ${NumberFormat.getInstance().format(data.harga)}"

                    // Detail pembayaran
                    binding.tvNamaPengirim.text =
                        "order id : ${data.orderId ?: "-"}"
                    binding.tvNamaPenerima.text = "nama penerima : dhuha group"
                    binding.tvTanggalBayar.text =
                        "tanggal bayar : ${data.tanggal ?: "-"}"
                    binding.tvMetodeBayar.text =
                        "metode pembayaran : ${data.metodePembayaran}"

                    // Status
                    setStatusAnimation(data.paymentStatus)
                }
            }
        }
        private fun setStatusAnimation(status: String) {

            binding.successAnim.cancelAnimation()
            binding.successAnim.clearAnimation()

            when (status.uppercase()) {
                "PAID", "SETTLEMENT", "CAPTURE" -> {
                    binding.successAnim.setAnimation(R.raw.succes)
                    binding.tvStatus.text = "PAID"
                    binding.layoutStatus.setBackgroundResource(R.drawable.bg_status_paid)
                    binding.imgStatus.setImageResource(R.drawable.ic_check)
                }

                "PENDING" -> {
                    binding.successAnim.setAnimation(R.raw.pending)
                    binding.tvStatus.text = "PENDING"
                    binding.layoutStatus.setBackgroundResource(R.drawable.bg_status_pending)
                    binding.imgStatus.setImageResource(R.drawable.ic_pending)
                }

                "FAILED", "CANCEL", "EXPIRE" -> {
                    binding.successAnim.setAnimation(R.raw.failed)
                    binding.tvStatus.text = "FAILED"
                    binding.layoutStatus.setBackgroundResource(R.drawable.bg_status_failed)
                    binding.imgStatus.setImageResource(R.drawable.ic_close)
                }

                else -> {
                    binding.successAnim.setAnimation(R.raw.pending)
                    binding.tvStatus.text = status
                    binding.layoutStatus.setBackgroundResource(R.drawable.bg_status_pending)
                }
            }

            binding.successAnim.playAnimation()
        }



    }





