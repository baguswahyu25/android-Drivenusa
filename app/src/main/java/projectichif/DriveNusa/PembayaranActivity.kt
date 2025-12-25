    package projectichif.DriveNusa.ui.pembayaran

    import android.content.Intent
    import android.os.Bundle
    import android.util.Log
    import android.view.View
    import android.widget.ImageView
    import android.widget.Toast
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.appcompat.app.AppCompatActivity
    import androidx.lifecycle.lifecycleScope
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch
    import projectichif.DriveNusa.ApiClient
    import projectichif.DriveNusa.PaymentActivity
    import projectichif.DriveNusa.R
    import projectichif.DriveNusa.api.SnapRequest
    import projectichif.DriveNusa.api.PaymentStatusResponse
    import projectichif.DriveNusa.databinding.ActivityPembayaranBinding


    class PembayaranActivity : AppCompatActivity() {

        private lateinit var binding: ActivityPembayaranBinding
        private var pendaftaranId: Int = 0
        private lateinit var metodePembayaran: String

        // Launcher untuk Midtrans
        private val paymentResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                // Jangan finish di sini, tunggu checkPaymentStatus
                checkPaymentStatus()
            }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            binding = ActivityPembayaranBinding.inflate(layoutInflater)
            setContentView(binding.root)

            pendaftaranId = intent.getIntExtra("extra_pendaftaran_id", 0)

            fetchPendaftaranDetail()

            val metodeFromIntent = intent.getStringExtra("extra_metode") ?: ""
            metodePembayaran = when (metodeFromIntent.lowercase()) {
                "transfer bank" -> "transfer_bank"
                "kredit" -> "kredit"
                "tunai" -> "tunai"
                else -> metodeFromIntent
            }
            binding.btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            if (metodePembayaran == "tunai") {
                finish()
                return
            }

            binding.btnBayarSekarang.setOnClickListener {
                setLoading(true)
                createSnapToken(pendaftaranId) { snapToken ->
                    setLoading(false)
                    val intent = Intent(this, PaymentActivity::class.java)
                    intent.putExtra("SNAP_TOKEN", snapToken)
                    paymentResultLauncher.launch(intent)
                }
            }
        }

        private fun fetchPendaftaranDetail() {
            setLoading(true)

            lifecycleScope.launch {
                try {
                    val response = ApiClient.authApi.getPendaftaranDetail(pendaftaranId)
                    setLoading(false)

                    if (response.isSuccessful) {
                        response.body()?.let { data ->
                            binding.txtNamaPaketBayar.text = data.paket_nama
                            binding.txtHargaPaketBayar.text = "Rp ${data.total}"
                            binding.txtMetodeBayar.text = data.metode
                            binding.txtTanggal.text = data.tanggal
                        }
                    } else {
                        showError("Gagal mengambil data pendaftaran")
                    }
                } catch (e: Exception) {
                    setLoading(false)
                    showError("Koneksi bermasalah")
                }
            }
        }


        private fun setLoading(isLoading: Boolean) {
            binding.progressBayar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnBayarSekarang.isEnabled = !isLoading
            binding.btnBayarSekarang.text =
                if (isLoading) "Memproses pembayaran..." else "Bayar Sekarang"
        }

        private fun showError(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        private fun createSnapToken(pendaftaranId: Int, onResult: (String) -> Unit) {
            lifecycleScope.launch {
                try {
                    val response = ApiClient.authApi.getSnapToken(
                        SnapRequest(
                            pendaftaran_id = pendaftaranId,
                            metode = metodePembayaran
                        )
                    )

                    if (response.isSuccessful) {
                        response.body()?.snap_token?.let { onResult(it) } ?: run {
                            setLoading(false)
                            showError("Token pembayaran tidak valid")
                        }
                    } else {
                        setLoading(false)
                        showError("Gagal membuat transaksi")
                    }

                } catch (e: Exception) {
                    setLoading(false)
                    showError("Koneksi bermasalah")
                }
            }
        }

        private fun checkPaymentStatus(retry: Int = 0) {
            setLoading(true)

            lifecycleScope.launch {
                try {
                    val response = ApiClient.authApi.checkPaymentStatus(pendaftaranId)
                    setLoading(false)

                    if (response.isSuccessful) {
                        val body = response.body() ?: return@launch
                        Log.d("PAYMENT_STATUS", response.body().toString())
                        when (body.status) {
                            "paid", "settlement", "capture" -> {
                                goToSuccess(
                                    body.paket_nama ?: "-",
                                    body.metode ?: "-",
                                    body.total ?: 0
                                )
                            }

                            "pending" -> {
                                if (retry < 3) {
                                    delay(1500)
                                    checkPaymentStatus(retry + 1)
                                } else {
                                    showError("Pembayaran masih diproses")
                                }
                            }

                            else -> showError("Pembayaran gagal")
                        }

                    }
                } catch (e: Exception) {
                    setLoading(false)
                    showError("Koneksi bermasalah")
                }
            }
        }


        private fun goToSuccess(paket: String, metode: String, total: Int) {
            val intent = Intent(this, PembayaranBerhasilActivity::class.java)
            intent.putExtra("extra_paket", paket)
            intent.putExtra("extra_metode", metode)
            intent.putExtra("extra_total", total)
            startActivity(intent)
            finish()
        }
    }
