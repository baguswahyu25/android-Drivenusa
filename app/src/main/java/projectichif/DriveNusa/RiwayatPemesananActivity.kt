package projectichif.DriveNusa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import projectichif.DriveNusa.databinding.ActivityRiwayatPemesananBinding

class RiwayatPemesananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatPemesananBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRiwayatPemesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        val data = listOf(
            RiwayatModel("Kursus mengemudi", "paket manual", "Rp 1.150.000", "13:00, 12-09-2025", R.drawable.img_paket_manual),
            RiwayatModel("Kursus mengemudi", "paket automatic", "Rp 1.250.000", "18:00, 08-09-2025", R.drawable.img_paket_automatic)
        )

        val adapter = RiwayatAdapter(data) { selected ->
            val intent = Intent(this, DetailPemesananActivity::class.java)
            intent.putExtra("judul", selected.judul)
            intent.putExtra("paket", selected.paket)
            intent.putExtra("harga", selected.harga)
            intent.putExtra("tanggal", selected.tanggal)
            intent.putExtra("gambar", selected.gambar)
            startActivity(intent)
        }

        binding.rvRiwayat.layoutManager = LinearLayoutManager(this)
        binding.rvRiwayat.adapter = adapter


    }
}
