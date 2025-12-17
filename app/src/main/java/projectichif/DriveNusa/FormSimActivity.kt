// Updated FormSimActivity with full Android-version compatibility
// (Android 5 - Android 14+) and modern file picker APIs

package projectichif.DriveNusa

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    private var tanggalLahirServer: String? = null


    // UNIVERSAL FILE PICKER (Works Android 5 – 14+)
    private val pickPasFoto =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                pasPhotoUri = uri
                binding.txtPasPhoto.text = getFileName(uri)
                binding.txtPasPhoto.setTextColor(
                    ContextCompat.getColor(this, android.R.color.black)
                )
            }
        }


    private val pickKtp =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                ktpUri = uri
                binding.txtKtpName.text = getFileName(uri)
                binding.txtKtpName.setTextColor(
                    ContextCompat.getColor(this, android.R.color.black)
                )
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormSimBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        paketNama = intent.getStringExtra(SyaratActivity.EXTRA_PAKET_NAMA)
        binding.tvPaket.text = "Paket yang dipilih: $paketNama"

        requestImagePermission()

        binding.cbKonfirmasi.setOnCheckedChangeListener { _, checked ->
            binding.btnSimpan.isEnabled = checked
        }

        binding.etTanggalLahir.setOnClickListener { showDatePicker() }
        val genderItems = listOf("Laki-laki", "Perempuan")

        val genderAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            genderItems
        )

        binding.etJenisKelamin.setAdapter(genderAdapter)

        binding.rgMetodePembayaran.setOnCheckedChangeListener { _, checkedId ->
            binding.llOpsiKredit.visibility = if (checkedId == R.id.rb_kredit) View.VISIBLE else View.GONE
        }

        // File Picker
        binding.btnUploadPasPhoto.setOnClickListener { pickPasFoto.launch("image/*") }
        binding.btnUploadKtp.setOnClickListener { pickKtp.launch("image/*") }

        binding.btnSimpan.setOnClickListener { submitFormSim() }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun requestImagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val perm = Manifest.permission.READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(perm), 1001)
            }
        } else {
            val perm = Manifest.permission.READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(perm), 1002)
            }
        }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        val dp = DatePickerDialog(
            this,
            { _, year, month, day ->
                val displayDate = "%02d/%02d/%04d".format(day, month + 1, year)
                binding.etTanggalLahir.setText(displayDate)

                // FORMAT UNTUK SERVER (🔥 PENTING)
                tanggalLahirServer = "%04d-%02d-%02d".format(year, month + 1, day)

            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dp.show()
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

        binding.btnSimpan.isEnabled = false
        binding.btnSimpan.text = "Mengirim..."

        val namaLengkap = binding.etNamaLengkap.text.toString().trim()
        val tempatLahir = binding.etTempatLahir.text.toString().trim()
        val tanggalLahir = tanggalLahirServer ?: ""
        if (tanggalLahirServer.isNullOrEmpty()) {
            Toast.makeText(this, "Pilih tanggal lahir!", Toast.LENGTH_SHORT).show()
            return
        }

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

        if (namaLengkap.isEmpty() || tempatLahir.isEmpty() || tanggalLahir.isEmpty() || alamat.isEmpty() || jenisKelamin.isEmpty() || pekerjaan.isEmpty() || mobil.isEmpty() || metode.isEmpty() || pasPhotoUri == null || ktpUri == null || (metode == "Kredit" && opsiKredit == null)) {

            Toast.makeText(this, "Lengkapi semua data & upload file!", Toast.LENGTH_SHORT).show()
            binding.btnSimpan.isEnabled = true
            binding.btnSimpan.text = "Simpan"
            return
        }

        // Harga Paket
        val hargaMap = mapOf(
            "Paket Manual" to 1150000,
            "Paket Automatic" to 1250000,
            "Paket Manual + SIM" to 2150000,
            "Paket Automatic + SIM" to 2200000
        )

        val harga = hargaMap[paketNama] ?: 0

        CoroutineScope(Dispatchers.IO).launch {

            val res = AuthRepository.submitFormSim(
                context = this@FormSimActivity,
                paket = paketNama ?: "",
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
                    Toast.makeText(this@FormSimActivity, "Berhasil!", Toast.LENGTH_LONG).show()

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
