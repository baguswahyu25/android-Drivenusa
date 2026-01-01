package projectichif.DriveNusa

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import projectichif.DriveNusa.databinding.ActivityFormPengajuanBinding
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.api.JadwalRequest
import projectichif.DriveNusa.api.PendaftaranAktifResponse
import java.util.Calendar

class FormPengajuanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormPengajuanBinding
    private var selectedTanggal: String? = null
    private var selectedJam: String? = null
    private var pendaftaranId: Int = -1
    private var pembayaranSuccess = false
    private var pendaftaranAktif: PendaftaranAktifResponse? = null

    private val api by lazy { ApiClient.authApi }

    private val listJam = listOf(
        "08:00","09:00","10:00","11:00",
        "13:00","14:00","15:00","16:00"
    )

    private val jamDipakai = hashMapOf<String, List<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormPengajuanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.layoutJam.visibility = View.GONE

        binding.txtPertemuan.text =
            intent.getStringExtra("pertemuan_nama") ?: "-"

        setupTanggalPicker()

        binding.btnAjukan.setOnClickListener {
            ajukanJadwal()
        }

        // Cek pembayaran & pendaftaran aktif
        cekStatusPembayaran()
    }

    // ============================
    // PICKER TANGGAL
    // ============================
    private fun setupTanggalPicker() {
        binding.btnTanggal.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    selectedTanggal = String.format("%04d-%02d-%02d", y, m + 1, d)
                    binding.btnTanggal.text = selectedTanggal
                    loadJamDipakai(selectedTanggal!!)
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    // ============================
    // LOAD JAM DARI API
    // ============================
    private fun loadJamDipakai(tanggal: String) {

        showJamShimmer(true) // 🔥 SHIMMER MULAI

        lifecycleScope.launch {
            try {
                val response = api.jamDipakai(tanggal)

                showJamShimmer(false) // 🔥 SHIMMER SELESAI

                if (response.isSuccessful) {
                    jamDipakai[tanggal] =
                        response.body()?.jam_dipakai ?: emptyList()
                    renderJamButtons()
                } else {
                    Toast.makeText(
                        this@FormPengajuanActivity,
                        "Gagal load jam",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                showJamShimmer(false)
                Toast.makeText(
                    this@FormPengajuanActivity,
                    "Error koneksi",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    // ============================
    // RENDER JAM
    // ============================
    private fun renderJamButtons() {
        binding.layoutJam.removeAllViews()
        val used = selectedTanggal?.let { jamDipakai[it] } ?: emptyList()

        listJam.forEach { jam ->
            val btn = Button(this)
            btn.text = jam
            btn.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            btn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_500)

            if (used.contains(jam)) {
                btn.isEnabled = false
                btn.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }

            btn.setOnClickListener {
                if (!pembayaranSuccess) {
                    Toast.makeText(this, "Selesaikan pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                selectedJam = jam
                resetAllJamButtons()
                btn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_700)
            }

            binding.layoutJam.addView(btn)
        }
    }

    private fun resetAllJamButtons() {
        for (i in 0 until binding.layoutJam.childCount) {
            val b = binding.layoutJam.getChildAt(i) as Button
            b.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_500)
        }
    }

    private fun disableSemuaJam() {
        binding.layoutJam.removeAllViews()
        listJam.forEach { jam ->
            val btn = Button(this)
            btn.text = jam
            btn.isEnabled = false
            btn.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            binding.layoutJam.addView(btn)
        }
    }
    private fun showJamShimmer(show: Boolean) {
        if (show) {
            binding.shimmerJam.visibility = View.VISIBLE
            binding.shimmerJam.startShimmer()
            binding.layoutJam.visibility = View.GONE
        } else {
            binding.shimmerJam.stopShimmer()
            binding.shimmerJam.visibility = View.GONE
            binding.layoutJam.visibility = View.VISIBLE
        }
    }

    // ============================
    // CEK STATUS PEMBAYARAN
    // ============================
    // ============================
// CEK STATUS PEMBAYARAN
// ============================
    private fun cekStatusPembayaran() {
        binding.btnAjukan.isEnabled = false // default disabled

        lifecycleScope.launch {
            try {
                val valid = AuthRepository.checkToken(this@FormPengajuanActivity)
                if (!valid) {
                    Toast.makeText(this@FormPengajuanActivity, "Sesi berakhir, silakan login ulang", Toast.LENGTH_LONG).show()
                    finish()
                    return@launch
                }

                val response = api.getPendaftaranAktif()
                if (response.isSuccessful) {
                    pendaftaranAktif = response.body()
                    Log.d("FormPengajuan", "Response aktif: $pendaftaranAktif")

                    if (pendaftaranAktif == null) {
                        Toast.makeText(this@FormPengajuanActivity, "Tidak ada pendaftaran aktif", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    pendaftaranId = pendaftaranAktif!!.id

                    // 🔥 Periksa status transaksi sesuai enum Midtrans
                    val status = pendaftaranAktif?.transaction?.transaction_status?.lowercase()
                    pembayaranSuccess = status in listOf("paid", "settlement", "capture")


                    Log.d("FormPengajuan", "pembayaranSuccess: $pembayaranSuccess (status: $status)")

                    if (!pembayaranSuccess) {
                        disableSemuaJam()
                        tampilkanInfoPembayaran()
                    } else {
                        binding.btnAjukan.isEnabled = true
                        renderJamButtons()
                    }

                } else {
                    Toast.makeText(this@FormPengajuanActivity, "Gagal mengambil status pendaftaran", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@FormPengajuanActivity, "Error koneksi", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }


    private fun tampilkanInfoPembayaran() {
        binding.btnBayar.visibility = View.VISIBLE
        binding.btnBayar.setOnClickListener {
            redirectKePembayaran()
        }
        Toast.makeText(this, "Selesaikan pembayaran untuk memilih jadwal", Toast.LENGTH_LONG).show()
    }

    private fun redirectKePembayaran() {
        val intent = Intent(this, PaymentActivity::class.java)
        intent.putExtra("pendaftaran_id", pendaftaranAktif!!.id)
        startActivity(intent)
    }

    // ============================
    // AJUKAN JADWAL
    // ============================
    private fun ajukanJadwal() {
        if (selectedTanggal == null || selectedJam == null) {
            Toast.makeText(this, "Pilih tanggal & jam", Toast.LENGTH_SHORT).show()
            return
        }

        val pertemuanKe = intent.getIntExtra("pertemuan_ke", -1)

        lifecycleScope.launch {
            if (!pembayaranSuccess) {
                Toast.makeText(this@FormPengajuanActivity, "Selesaikan pembayaran terlebih dahulu", Toast.LENGTH_LONG).show()
                return@launch
            }

            try {
                val response = api.ajukanJadwal(
                    JadwalRequest(
                        pendaftaran_id = pendaftaranId,
                        pertemuan_ke = pertemuanKe,
                        tanggal = selectedTanggal!!,
                        jam = selectedJam!!
                    )
                )

                if (response.isSuccessful) {
                    Toast.makeText(this@FormPengajuanActivity, "Pengajuan berhasil", Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    val code = response.code()
                    val msg = when (code) {
                        403 -> "Pembayaran belum selesai"
                        409 -> "Jam sudah dipakai"
                        422 -> "Data jadwal tidak valid"
                        else -> "Gagal mengajukan jadwal"
                    }
                    Toast.makeText(this@FormPengajuanActivity, msg, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@FormPengajuanActivity, "Error koneksi", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}
