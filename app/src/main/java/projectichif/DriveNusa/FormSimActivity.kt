package projectichif.DriveNusa

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import projectichif.DriveNusa.api.AuthRepository
import projectichif.DriveNusa.databinding.ActivityFormSimBinding
import projectichif.DriveNusa.ui.pembayaran.PembayaranActivity
import java.text.SimpleDateFormat
import java.util.*

class FormSimActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormSimBinding
    private var paketNama: String? = null
    private var pasPhotoUri: Uri? = null
    private var ktpUri: Uri? = null

    private val PICK_PAS = 201
    private val PICK_KTP = 202

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormSimBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        paketNama = intent.getStringExtra(SyaratActivity.EXTRA_PAKET_NAMA)
        binding.tvPaket.text = "Paket yang dipilih: $paketNama"

        binding.cbKonfirmasi.setOnCheckedChangeListener { _, isChecked ->
            binding.btnSimpan.isEnabled = isChecked
        }

        binding.etTanggalLahir.setOnClickListener { showDatePicker() }

        binding.rgMetodePembayaran.setOnCheckedChangeListener { _, checkedId ->
            binding.llOpsiKredit.visibility = if (checkedId == R.id.rb_kredit) View.VISIBLE else View.GONE
        }

        binding.btnUploadPasPhoto.setOnClickListener { pickFile(PICK_PAS) }
        binding.btnUploadKtp.setOnClickListener { pickFile(PICK_KTP) }

        binding.btnSimpan.setOnClickListener { submitFormSim() }

        binding.btnBack.setOnClickListener { finish() }
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

    private fun pickFile(code: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Pilih file"), code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val uri = data.data ?: return
            when (requestCode) {
                PICK_PAS -> {
                    pasPhotoUri = uri
                    binding.txtPasPhoto.text = getFileName(uri)
                }
                PICK_KTP -> {
                    ktpUri = uri
                    binding.txtKtpName.text = getFileName(uri)
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "file"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && idx >= 0) name = it.getString(idx)
        }
        return name
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return sdf.format(Date())
    }

    private fun submitFormSim() {
        // Disable tombol dulu biar aman
        binding.btnSimpan.isEnabled = false
        binding.btnSimpan.text = "Mengirim..."

        // Ambil semua field saat tombol diklik (pasti terbaca)
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
            mobil.isEmpty() || metode.isEmpty() || pasPhotoUri == null || ktpUri == null ||
            (metode == "Kredit" && opsiKredit == null)
        ) {
            Toast.makeText(this, "Lengkapi semua data dan file wajib diunggah!", Toast.LENGTH_SHORT).show()
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
            val res = AuthRepository.submitFormSim(
                context = this@FormSimActivity,
                paket = paketNama ?: "Tidak diketahui",
                namaLengkap = namaLengkap,
                tempatLahir = tempatLahir,
                tanggalLahir = tanggalLahir,
                alamat = alamat,
                jenisKelamin = jenisKelamin,
                pekerjaan = pekerjaan,
                mobilDipilih = mobil,
                metodePembayaran = metode,
                opsiKredit = opsiKredit,
                harga = harga,
                pasPhotoUri = pasPhotoUri!!,
                ktpUri = ktpUri!!
            )

            runOnUiThread {
                binding.btnSimpan.isEnabled = true
                binding.btnSimpan.text = "Simpan"

                if (res?.success == true) {
                    Toast.makeText(this@FormSimActivity, "Berhasil menyimpan data!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@FormSimActivity, PembayaranActivity::class.java).apply {
                        putExtra("extra_paket", paketNama)
                        putExtra("extra_harga", harga)
                        putExtra("extra_metode", metode)
                        putExtra("extra_tanggal", getCurrentDate())
                        putExtra("extra_penerima_bank", "Bank BCA")
                        putExtra("extra_penerima_nama", "Drive Nusa")
                        putExtra("extra_penerima_nomor", "1234567890")
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this@FormSimActivity, "Error: ${res?.message ?: "Server Error"}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
