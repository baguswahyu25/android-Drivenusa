package projectichif.DriveNusa.ui.pembayaran

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import projectichif.DriveNusa.databinding.ActivityPembayaranBinding

class PembayaranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPembayaranBinding
    private val PICK_FILE_REQUEST = 101
    private var buktiUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol back
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnUploadBukti.setOnClickListener {
            pickFile()
        }

        // === AMBIL DATA DARI FormActivity ===
        val namaPaket = intent.getStringExtra("extra_paket")
        val hargaPaket = intent.getIntExtra("extra_harga", 0)
        val metodePembayaran = intent.getStringExtra("extra_metode")
        val tanggal = intent.getStringExtra("extra_tanggal")

        val penerimaBank = intent.getStringExtra("extra_penerima_bank")
        val penerimaNama = intent.getStringExtra("extra_penerima_nama")
        val penerimaRek = intent.getStringExtra("extra_penerima_nomor")

        // === TAMPILKAN DATA ===
        binding.txtNamaPaketBayar.text = namaPaket ?: "-"
        binding.txtHargaPaketBayar.text = "Rp. $hargaPaket"
        binding.txtMetodeBayar.text = metodePembayaran ?: "-"
        binding.txtTanggal.text = tanggal ?: "-"

        binding.txtNamaBank.text = penerimaBank ?: "-"
        binding.txtPenerima.text = penerimaNama ?: "-"
        binding.txtNomorRek.text = penerimaRek ?: "-"

        // === LOGIKA UTAMA: TAMPILAN BERDASARKAN METODE PEMBAYARAN ===
        when (metodePembayaran) {

            "Transfer Bank" -> {
                // tampilkan info transfer
                binding.layoutTransferInfo.visibility = View.VISIBLE
                binding.btnUploadBukti.visibility = View.VISIBLE

                // untuk transfer btn bayar tetap ada
                binding.btnBayarSekarang.visibility = View.VISIBLE
            }

            "Tunai" -> {
                // sembunyikan semua info transfer
                binding.layoutTransferInfo.visibility = View.GONE
                binding.btnUploadBukti.visibility = View.GONE

                // tetap tampilkan tombol bayar
                binding.btnBayarSekarang.visibility = View.VISIBLE
            }

            "Kredit" -> {
                // kredit tidak butuh upload bukti & tidak butuh info bank
                binding.layoutTransferInfo.visibility = View.GONE
                binding.btnUploadBukti.visibility = View.GONE

                // tombol bayar berubah menjadi tombol ajukan kredit
                binding.btnBayarSekarang.text = "Ajukan Kredit"

                binding.btnBayarSekarang.setOnClickListener {
                    // buka halaman kredit
                    val i = Intent(this, PembayaranKreditActivity::class.java)
                    i.putExtra("harga_paket", hargaPaket)
                    i.putExtra("nama_paket", namaPaket)
                    startActivity(i)
                }

                return // mencegah click listener double
            }
        }



        // === DEFAULT BUTTON TRANSFER / TUNAI ===
        binding.btnBayarSekarang.setOnClickListener {
            Toast.makeText(
                this,
                "Pembayaran untuk $namaPaket sedang diproses...",
                Toast.LENGTH_LONG
            ).show()
        }

    }
    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*" // bisa diganti "image/*" jika hanya gambar
        }
        startActivityForResult(Intent.createChooser(intent, "Pilih Bukti Transfer"), PICK_FILE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val uri = data.data ?: return
            if (requestCode == PICK_FILE_REQUEST) {
                buktiUri = uri
                // tampilkan nama file di txtBukti
                binding.txtBukti.text = getFileName(uri)
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "file"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val idx = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && idx >= 0) {
                name = it.getString(idx)
            }
        }
        return name
    }

}
