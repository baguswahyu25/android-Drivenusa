package projectichif.DriveNusa

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        paketNama = intent.getStringExtra(EXTRA_PAKET_NAMA)
        binding.tvPaket.text = "Paket yang dipilih: $paketNama"

        binding.headerBar.findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSimpan.isEnabled = false
        binding.cbKonfirmasi.setOnCheckedChangeListener { _, isChecked ->
            binding.btnSimpan.isEnabled = isChecked
        }

        binding.etTanggalLahir.setOnClickListener { showDatePicker() }

        binding.rgMetodePembayaran.setOnCheckedChangeListener { _, checkedId ->
            binding.llOpsiKredit.visibility = if (checkedId == R.id.rb_kredit) View.VISIBLE else View.GONE
        }

        binding.btnSimpan.setOnClickListener { submitForm() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->
                val formatted = "%02d/%02d/%04d".format(day, month + 1, year)
                binding.etTanggalLahir.setText(formatted)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return sdf.format(Date())
    }

    private fun submitForm() {
        // Disable tombol dulu
        binding.btnSimpan.isEnabled = false
        binding.btnSimpan.text = "Mengirim..."

        // Ambil semua field saat tombol diklik
        val namaLengkap = binding.etNamaLengkap.text.toString().trim()
        val tempatLahir = binding.etTempatLahir.text.toString().trim()
        val tanggalLahir = binding.etTanggalLahir.text.toString().trim()
        val alamat = binding.etAlamat.text.toString().trim()
        val jenisKelamin = binding.etJenisKelamin.text.toString().trim()
        val pekerjaan = binding.etPekerjaan.text.toString().trim()

        val mobil = when (binding.rgPilihMobil.checkedRadioButtonId) {
            R.id.rb_avanza -> "Toyota Avanza"
            R.id.rb_ayla -> "Daihatsu Ayla"
            R.id.rb_calya -> "Toyota Calya"
            R.id.rb_brio -> "Honda Brio"
            R.id.rb_mobilio -> "Honda Mobilio"
            else -> ""
        }

        val metode = when (binding.rgMetodePembayaran.checkedRadioButtonId) {
            R.id.rb_transfer_bank -> "Transfer Bank"
            R.id.rb_kredit -> "Kredit"
            R.id.rb_tunai -> "Tunai"
            else -> ""
        }

        val opsiKredit = when (binding.rgOpsiKredit.checkedRadioButtonId) {
            R.id.rb_kredit_minggu -> "Per minggu"
            R.id.rb_kredit_bulan -> "Per bulan"
            R.id.rb_kredit_tahun -> "Per tahun"
            else -> null
        }

        // VALIDASI
        if (namaLengkap.isEmpty() || tempatLahir.isEmpty() || tanggalLahir.isEmpty() ||
            alamat.isEmpty() || jenisKelamin.isEmpty() || pekerjaan.isEmpty() ||
            mobil.isEmpty() || metode.isEmpty() || (metode == "Kredit" && opsiKredit == null)
        ) {
            Toast.makeText(this, "Lengkapi semua data!", Toast.LENGTH_SHORT).show()
            binding.btnSimpan.isEnabled = true
            binding.btnSimpan.text = "Simpan"
            return
        }

        val hargaMap = mapOf(
            "Paket Manual" to 1150000,
            "Paket Automatic" to 1250000,
            "Paket Manual + SIM" to 2150000,
            "Paket Automatic + SIM" to 2200000
        )

        val harga = hargaMap[paketNama] ?: 0

        // Coroutine untuk submit
        CoroutineScope(Dispatchers.IO).launch {
            val formRequest = FormRequest(
                paket = paketNama ?: "",
                nama_lengkap = namaLengkap,
                tempat_lahir = tempatLahir,
                tanggal_lahir = tanggalLahir,
                alamat = alamat,
                jenis_kelamin = jenisKelamin,
                pekerjaan = pekerjaan,
                mobil_dipilih = mobil,
                metode_pembayaran = metode,
                opsi_kredit = opsiKredit,
                harga = harga
            )

            val res = AuthRepository.submitForm(this@FormActivity, formRequest)

            runOnUiThread {
                binding.btnSimpan.isEnabled = true
                binding.btnSimpan.text = "Simpan"

                if (res?.success == true) {
                    Toast.makeText(this@FormActivity, "Berhasil menyimpan data!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@FormActivity, PembayaranActivity::class.java).apply {
                        putExtra("extra_paket", paketNama)
                        putExtra("extra_harga", harga)
                        putExtra("extra_metode", metode)
                        putExtra("extra_tanggal", getCurrentDate())
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this@FormActivity, "Error: ${res?.message ?: "Server Error"}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
