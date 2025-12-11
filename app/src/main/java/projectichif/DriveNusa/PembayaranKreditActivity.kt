package projectichif.DriveNusa.ui.pembayaran

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import projectichif.DriveNusa.databinding.PembayaranKreditBinding

class PembayaranKreditActivity : AppCompatActivity() {

    private lateinit var binding: PembayaranKreditBinding
    private var hargaPaket = 0
    private val dp = 500000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PembayaranKreditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hargaPaket = intent.getIntExtra("harga_paket", 0)

        binding.txtHargaKredit.text = formatRupiah(hargaPaket)
        binding.txtDP.text = formatRupiah(dp)

        binding.groupTenor.setOnCheckedChangeListener { _, id ->
            hitungCicilan(id)
        }
        binding.btnBackKredit.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnAjukanKredit.setOnClickListener {

            val intent = Intent(this, KonfirmasiKreditActivity::class.java).apply {
                putExtra("paket_nama", intent.getStringExtra("nama_paket"))
                putExtra("harga_paket", hargaPaket)
                putExtra("dp", dp)
                putExtra("tenor", binding.optionMinggu.text.toString()) // diganti otomatis di bawah

                val selectedTenor = when (binding.groupTenor.checkedRadioButtonId) {
                    binding.optionMinggu.id -> "Per Minggu (4x)"
                    binding.optionBulan.id -> "Per Bulan (6x)"
                    else -> "Per Tahun (12x)"
                }

                putExtra("tenor", selectedTenor)
                putExtra("total_cicilan", (hargaPaket - dp))
                putExtra("nominal_periode", binding.txtNominalPeriode.text.toString().replace("Rp ", "").replace(".", "").toIntOrNull() ?: 0)
            }

            startActivity(intent)
        }

    }

    private fun hitungCicilan(id: Int) {
        val sisa = hargaPaket - dp

        var tenor = 1

        when (id) {
            binding.optionMinggu.id -> tenor = 4
            binding.optionBulan.id -> tenor = 6
            binding.optionTahun.id -> tenor = 12
        }

        val perCicilan = sisa / tenor

        binding.txtTotalCicilan.text = formatRupiah(sisa)
        binding.txtNominalPeriode.text = formatRupiah(perCicilan)
    }

    private fun formatRupiah(value: Int): String {
        return "Rp %,d".format(value).replace(",", ".")
    }
}
