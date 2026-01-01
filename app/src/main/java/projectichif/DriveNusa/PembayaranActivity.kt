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
    
            private var pendaftaranId = 0
            private var nextTransactionId = 0
            private var metodePembayaran = ""
            private var initialTransactionId = 0


            private val paymentResultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    // selesai dari Midtrans → cek status
                    showPaymentLoading(true)
                    checkPaymentStatus()
                }

    
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                binding = ActivityPembayaranBinding.inflate(layoutInflater)
                setContentView(binding.root)
    
                pendaftaranId = intent.getIntExtra("extra_pendaftaran_id", 0)
                metodePembayaran = intent.getStringExtra("extra_metode")?.lowercase() ?: ""
                initialTransactionId = intent.getIntExtra("extra_transaction_id", 0)
                metodePembayaran = when (metodePembayaran) {
                    "transfer bank" -> "transfer_bank"
                    "kredit" -> "kredit"
                    else -> metodePembayaran
                }
    
                if (pendaftaranId == 0) {
                    toast("ID pendaftaran tidak valid")
                    finish()
                    return
                }
    
                binding.btnBack.setOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
                fetchPendaftaranDetail()

                when (metodePembayaran) {

                    "tunai" -> {
                        toast("Silakan lakukan pembayaran tunai ke toko kami")
                        binding.btnBayarSekarang.visibility = View.GONE
                        binding.txtInfoPembayaran.visibility = View.GONE
                    }

                    "transfer_bank" -> {
                        // 🔥 TRANSFER BANK = TRANSAKSI PERTAMA SAJA
                        nextTransactionId = initialTransactionId
                        binding.txtInfoPembayaran.visibility = View.GONE
                    }

                    "kredit" -> {
                        // 🔁 KREDIT = DP + CICILAN
                        if (initialTransactionId != 0) {
                            nextTransactionId = initialTransactionId
                            binding.txtInfoPembayaran.text = "Pembayaran DP"
                        } else {
                            fetchNextTransaction()
                        }
                        binding.txtInfoPembayaran.visibility = View.VISIBLE
                    }
                }

                binding.txtLoadingInfo.text =
                    if (metodePembayaran == "kredit") "Memproses pembayaran cicilan..."
                    else "Memproses pembayaran..."


                binding.btnBayarSekarang.setOnClickListener {
                    if (nextTransactionId == 0) {
                        toast("Transaksi belum siap")
                        return@setOnClickListener
                    }
                    showPaymentLoading(true)
                    createSnapToken()
                }
    
            }

            private fun showPaymentLoading(show: Boolean) {
                binding.loadingOverlay.visibility = if (show) View.VISIBLE else View.GONE
            }

            // =========================
            // FETCH DATA
            // =========================
    
            private fun fetchPendaftaranDetail() {
                setLoading(true)
                lifecycleScope.launch {
                    try {
                        val res = ApiClient.authApi.getPendaftaranDetail(pendaftaranId)
                        setLoading(false)
    
                        if (res.isSuccessful) {
                            res.body()?.let {
                                binding.txtNamaPaketBayar.text = it.paket_nama
                                binding.txtHargaPaketBayar.text = "Rp ${it.total}"
                                binding.txtMetodeBayar.text = it.metode
                                binding.txtTanggal.text = it.tanggal
                            }
                        }
                    } catch (e: Exception) {
                        setLoading(false)
                        toast("Gagal memuat data")
                    }
                }
            }
    
            private fun fetchNextTransaction() {
                lifecycleScope.launch {
                    try {
                        val res = ApiClient.authApi.getNextTransaction(pendaftaranId)
                        if (res.isSuccessful) {
                            res.body()?.let {
                                nextTransactionId = it.transaction_id
                                binding.txtInfoPembayaran.text =
                                    when (it.type) {
                                        "dp" -> "Pembayaran DP: Rp ${it.amount}"
                                        "cicilan" -> "Cicilan ${it.cicilan_ke}/${it.total_cicilan}: Rp ${it.amount}"
                                        else -> "Pembayaran"
                                    }
    
                            }
                        }
                    } catch (_: Exception) {}
                }
            }
    
            // =========================
            // PAYMENT
            // =========================
    
            private fun createSnapToken() {
                setLoading(true)
    
                lifecycleScope.launch {
                    try {
                        val res = ApiClient.authApi.getSnapToken(
                            SnapRequest(
                                transaction_id = nextTransactionId,
                                metode = metodePembayaran
                            )
                        )
    
                        setLoading(false)
    
                        if (res.isSuccessful) {
                            res.body()?.snap_token?.let {
                                val intent = Intent(this@PembayaranActivity, PaymentActivity::class.java)
                                intent.putExtra("SNAP_TOKEN", it)
                                paymentResultLauncher.launch(intent)
                            }
                        } else {
                            toast("Gagal membuat pembayaran")
                        }
    
                    } catch (e: Exception) {
                        setLoading(false)
                        toast("Koneksi bermasalah")
                    }
                }
            }

            private fun checkPaymentStatus(retry: Int = 0) {
                lifecycleScope.launch {
                    try {
                        val res = ApiClient.authApi.checkPaymentStatus(pendaftaranId)

                        if (res.isSuccessful) {
                            val body = res.body() ?: return@launch

                            when (body.status) {

                                "paid", "settlement", "capture" -> {
                                    showPaymentLoading(false)
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
                                        showPaymentLoading(false)
                                        toast("Pembayaran sedang diproses")
                                    }
                                }

                                else -> {
                                    showPaymentLoading(false)
                                    toast("Pembayaran gagal")
                                }
                            }
                        } else {
                            showPaymentLoading(false)
                            toast("Gagal cek status")
                        }

                    } catch (e: Exception) {
                        showPaymentLoading(false)
                        toast("Gagal cek status")
                    }
                }
            }


            // =========================
            // HELPERS
            // =========================
    
            private fun setLoading(state: Boolean) {
                binding.progressBayar.visibility = if (state) View.VISIBLE else View.GONE
                binding.btnBayarSekarang.isEnabled = !state
            }
    
            private fun toast(msg: String) =
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    
            private fun goToSuccess(paket: String, metode: String, total: Int) {
                startActivity(
                    Intent(this, PembayaranBerhasilActivity::class.java).apply {
                        putExtra("extra_paket", paket)
                        putExtra("extra_metode", metode)
                        putExtra("extra_total", total)
                    }
                )
                finish()
            }
        }
