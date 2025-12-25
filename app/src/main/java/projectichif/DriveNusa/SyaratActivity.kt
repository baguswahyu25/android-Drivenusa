package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import projectichif.DriveNusa.databinding.ActivitySyaratBinding

class SyaratActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySyaratBinding

    companion object {
        const val EXTRA_PAKET_NAMA = "extra_paket_nama"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySyaratBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Ambil nama paket dari intent
        val paketNama = intent.getStringExtra(EXTRA_PAKET_NAMA)

        binding.btnLanjutkan.isEnabled = false

        // Checkbox setuju
        binding.cbSetuju.setOnCheckedChangeListener { _, isChecked ->
            binding.btnLanjutkan.isEnabled = isChecked
            if (isChecked) {
                Toast.makeText(this, "Melanjutkan paket: $paketNama", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLanjutkan.setOnClickListener {

            if (paketNama.isNullOrEmpty()) {
                Toast.makeText(this, "Paket tidak ditemukan!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val paket = paketNama.lowercase()

            val jenisPaket = when {
                paket.contains("automatic") -> "automatic"
                else -> "manual"
            }

            val tipePendaftaran = if (paket.contains("sim")) "sim" else "non_sim"


            val intent = Intent(this, FormActivity::class.java)
            intent.putExtra(EXTRA_PAKET_NAMA, paketNama)
            intent.putExtra("extra_tipe_pendaftaran", tipePendaftaran)
            intent.putExtra("extra_jenis_paket", jenisPaket)

            startActivity(intent)
        }
    }override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}
