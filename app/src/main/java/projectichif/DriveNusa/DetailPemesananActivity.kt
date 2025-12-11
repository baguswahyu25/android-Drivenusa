package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import projectichif.DriveNusa.databinding.ActivityDetailPemesananBinding

class DetailPemesananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPemesananBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari adapter
        val judul = intent.getStringExtra("judul")
        val paket = intent.getStringExtra("paket")
        val harga = intent.getStringExtra("harga")
        val tanggal = intent.getStringExtra("tanggal")
        val gambar = intent.getIntExtra("gambar", 0)

        // Set data
        binding.tvJudul.text = judul
        binding.tvPaket.text = paket
        binding.tvHarga.text = harga
        binding.tvTanggalBayar.text = "Tanggal bayar: $tanggal"

        if (gambar != 0) binding.imgMobil.setImageResource(gambar)

        binding.btnPengajuan.setOnClickListener {
            val intent = Intent(this, PengajuanJadwalActivity::class.java)
            startActivity(intent)
        }

        binding.btnBeranda.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        }
    }

