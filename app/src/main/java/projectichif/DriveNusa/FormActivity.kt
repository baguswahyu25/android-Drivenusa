package projectichif.DriveNusa

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.api.FormRequest
import projectichif.DriveNusa.databinding.ActivityFormBinding
import projectichif.DriveNusa.ui.pembayaran.PembayaranActivity
import projectichif.DriveNusa.SyaratActivity.Companion.EXTRA_PAKET_NAMA
import java.text.SimpleDateFormat
import java.util.*

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private var paketNama: String? = null
    private var tanggalLahirServer: String? = null
    private var hargaPaket: Int = 0
    private var tipePendaftaran: String = "non_sim"

    private var jenisPaket: String = "manual"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Safe hide ActionBar for all Android versions
        try {
            supportActionBar?.hide()
        } catch (_: Exception) {
        }
        paketNama = intent.getStringExtra(EXTRA_PAKET_NAMA)

        if (paketNama.isNullOrBlank()) {
            Toast.makeText(this, "Paket tidak ditemukan", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        hargaPaket = intent.getIntExtra("extra_harga", 0)

        binding.tvPaket.text = "Paket: $paketNama"
        tipePendaftaran = intent.getStringExtra("extra_tipe_pendaftaran") ?: "non_sim"

        // Safe back button
        binding.headerBar.findViewById<ImageView>(R.id.btn_back)?.setOnClickListener {
            try {
                onBackPressedDispatcher.onBackPressed()
            } catch (_: Exception) {
                finish()
            }
        }
        jenisPaket = intent.getStringExtra("extra_jenis_paket") ?: "manual"

        // Disable save until confirmed
        binding.btnSimpan.isEnabled = false
        binding.cbKonfirmasi.setOnCheckedChangeListener { _, isChecked ->
                binding.btnSimpan.isEnabled = isChecked
        }


        binding.etTanggalLahir.setOnClickListener { showDatePickerSafe() }
        val genderItems = listOf("Laki-laki", "Perempuan")

        val genderAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            genderItems
        )

        binding.etJenisKelamin.setAdapter(genderAdapter)
        // Transmisi → mobil
        setupMobilByPaket()


        binding.btnSimpan.setOnClickListener { submitFormSafe() }
    }

    // Safe DatePicker for all Android versions
    private fun showDatePickerSafe() {
        try {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val displayDate = "%02d/%02d/%04d".format(day, month + 1, year)
                    binding.etTanggalLahir.setText(displayDate)

                    // FORMAT UNTUK SERVER (🔥 PENTING)
                    tanggalLahirServer = "%04d-%02d-%02d".format(year, month + 1, day)

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal membuka date picker", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentDate(): String {
        return try {
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            sdf.format(Date())
        } catch (e: Exception) {
            "-"
        }
    }

    private fun setupMobilByPaket() {
        val mobilList = if (jenisPaket == "automatic") {
            listOf("Nissan Livina")
        } else {
            listOf("Daihatsu Ayla", "Daihatsu Sigra")
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            mobilList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMobil.adapter = adapter
        binding.spinnerMobil.isEnabled = mobilList.size > 1

    }
    private fun setLoading(isLoading: Boolean) {
        binding.loadingOverlay.visibility =
            if (isLoading) View.VISIBLE else View.GONE

        binding.btnSimpan.isEnabled = !isLoading
        binding.btnSimpan.alpha = if (isLoading) 0.7f else 1f
    }




    private fun submitFormSafe() {

        val namaLengkap = binding.etNamaLengkap.text.toString().trim()
        val tempatLahir = binding.etTempatLahir.text.toString().trim()
        val tanggalLahir = tanggalLahirServer ?: ""
        val alamat = binding.etAlamat.text.toString().trim()
        val jenisKelamin = binding.etJenisKelamin.text.toString().trim()
        val pekerjaan = binding.etPekerjaan.text.toString().trim()
        val mobil = binding.spinnerMobil.selectedItem?.toString() ?: ""

        val metode = when (binding.rgMetodePembayaran.checkedRadioButtonId) {
            R.id.rb_transfer_bank -> "Transfer Bank"
            R.id.rb_kredit -> "Kredit"
            R.id.rb_tunai -> "Tunai"
            else -> ""
        }

        if (namaLengkap.isEmpty() || tempatLahir.isEmpty() || tanggalLahir.isEmpty() ||
            alamat.isEmpty() || jenisKelamin.isEmpty() || pekerjaan.isEmpty() ||
            mobil.isEmpty() || metode.isEmpty()
        ) {
            Toast.makeText(this, "Lengkapi semua data", Toast.LENGTH_SHORT).show()
            return
        }
        setLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            val request = FormRequest(
                paket = paketNama!!,
                nama_lengkap = namaLengkap,
                tempat_lahir = tempatLahir,
                tanggal_lahir = tanggalLahir,
                alamat = alamat,
                jenis_kelamin = jenisKelamin,
                pekerjaan = pekerjaan,
                mobil_dipilih = mobil,
                metode_pembayaran = metode,
                tipe_pendaftaran = tipePendaftaran
            )

            val res = AuthRepository.submitForm(this@FormActivity, request)

            runOnUiThread {
                setLoading(false)

                if (res?.success == true) {
                    val intent = Intent(this@FormActivity, PembayaranActivity::class.java)
                    intent.putExtra("extra_pendaftaran_id", res.pendaftaran_id)
                    intent.putExtra("extra_metode", metode)
                    intent.putExtra("extra_transaction_id", res.transaction_id)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@FormActivity,
                        res?.message ?: "Server error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun resetButton() {
        binding.btnSimpan.isEnabled = true
        binding.btnSimpan.text = "Simpan"
    }

}

