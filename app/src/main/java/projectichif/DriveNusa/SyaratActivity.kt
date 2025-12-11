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

        supportActionBar?.hide()

        // Ambil nama paket yang dikirim dari Fragment
        val paketNama = intent.getStringExtra(EXTRA_PAKET_NAMA)

        binding.btnLanjutkan.isEnabled = false

        // Ketika checkbox "setuju" dicentang → aktifkan tombol
        binding.cbSetuju.setOnCheckedChangeListener { _, isChecked ->
            binding.btnLanjutkan.isEnabled = isChecked
            if (isChecked) {
                Toast.makeText(this, "Melanjutkan paket: $paketNama", Toast.LENGTH_SHORT).show()
            }
        }

        // Logika penentuan FormActivity / FormSimActivity
        binding.btnLanjutkan.setOnClickListener {
            if (paketNama == null) {
                Toast.makeText(this, "Paket tidak ditemukan!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // *** Routing berdasarkan nama paket ***
            val isSim = paketNama.contains("SIM", ignoreCase = true)

            val targetActivity = if (isSim) {
                FormSimActivity::class.java
            } else {
                FormActivity::class.java
            }

            val intent = Intent(this, targetActivity)
            intent.putExtra(EXTRA_PAKET_NAMA, paketNama)
            startActivity(intent)
        }

        // Tombol Kembali
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
