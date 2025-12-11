package projectichif.DriveNusa.ui.pembayaran

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import projectichif.DriveNusa.databinding.KonfirmasiKreditBinding

class KonfirmasiKreditActivity : AppCompatActivity() {

    private lateinit var binding: KonfirmasiKreditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KonfirmasiKreditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BACK BUTTON
        binding.btnBackKonfirmasi.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Ambil data dari PembayaranKreditActivity
        val paketNama = intent.getStringExtra("paket_nama") ?: "-"
        val harga = intent.getIntExtra("harga_paket", 0)
        val dp = intent.getIntExtra("dp", 0)
        val tenorText = intent.getStringExtra("tenor") ?: "-"
        val totalCicilan = intent.getIntExtra("total_cicilan", 0)
        val nominalPeriode = intent.getIntExtra("nominal_periode", 0)

        // Tampilkan ke UI
        binding.txtNamaPaket.text = paketNama
        binding.txtHargaPaket.text = formatRupiah(harga)
        binding.txtDP.text = formatRupiah(dp)
        binding.txtTenor.text = tenorText
        binding.txtTotalCicilan.text = formatRupiah(totalCicilan)
        binding.txtNominalPeriode.text = formatRupiah(nominalPeriode)

        // Tombol Kirim Pengajuan
        binding.btnKirimPengajuan.setOnClickListener {
            Toast.makeText(this, "Pengajuan kredit telah dikirim!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun formatRupiah(value: Int): String {
        return "Rp %,d".format(value).replace(",", ".")
    }
}
